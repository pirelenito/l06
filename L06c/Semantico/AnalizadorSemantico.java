package Semantico;

import Sintatico.ArvorePrograma;

public class AnalizadorSemantico {
	
	private Declaracoes declaracoes;
		
	private void caminhaArvore ( )
	{
		
		
	}
	
	public void validaArvore(ArvorePrograma arvorePrograma) throws Exception{		
	
		programa ( arvorePrograma );		
	}
		
	private void programa ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		///declaracoes.declara ( no.valor, no.pai, escopoAtual, new Tipo ( Tipo.TIPO_PROGRAMA ) );

		no = no.irmao;
		bloco ( no );
	}
	
	/*
	private void identificador ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "identificador" );
		
		validaToken ( "TO_ID", 0 );

		eu.valor = token[1];
		
		return eu;
	}

	private void numero ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "numero" );
		
		validaToken ( "TO_IN" , 0 );

		eu.valor = token[1];
		
		return eu;
	}*/

	private void bloco ( ArvorePrograma no ) throws Exception
	{	
		declaracoes.sobeEscopo ( );
		
		no = no.filho;
		declaracoes ( no );	
			
		no = no.irmao;
		comando_composto ( no );
					
		declaracoes.desceEscopo ( );
	}
	
	private void declaracoes ( ArvorePrograma no ) throws Exception
	{	
		no = no.filho;		
		
		if ( no != null && no.nomeNo == "parte_rotulos" )
		{
			parte_rotulos ( no );
			no = no.irmao;
		}
		
		if ( no != null && no.nomeNo == "parte_const" )
		{
			parte_const ( no );
			no = no.irmao;
		}
		
		if ( no != null && no.nomeNo == "parte_tipos" )
		{
			parte_tipos ( no );
			no = no.irmao;
		}
		
		if ( no != null && no.nomeNo == "parte_vars" )
		{
			parte_vars ( no );
			no = no.irmao;
		}
		
		if ( no != null && no.nomeNo == "parte_rotinas" )
		{
			parte_rotinas ( no );
			no = no.irmao;
		}
		
	}


	private void parte_rotulos ( ArvorePrograma no ) throws Exception
	{	
		no = no.filho;
		
		do
		{
			if ( !declaracoes.declaraRotulo ( no.valor ) )
				throw new Exception ( "Dupla declaracao do rotulo" + no.valor );
			
			no = no.irmao;
		}
		while ( no != null );
		
	}

	private void parte_const ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		do
		{
			decl_cte ( no );
			no = no.irmao;
		}		
		while ( no != null );	
	}

	/**
	 * 
	 * @param no
	 * @throws Exception
	 * @todo implementar skema para verificar se expressao é constante!
	 */
	private void decl_cte ( ArvorePrograma no ) throws Exception
	{
		//pego o nome da constante
		no = no.filho;		
		String identificador = no.valor;
		
		//pego o tipo da expressao
		no = no.irmao;
		Tipo tipoExpressao = expressao(no);
				
		declaracoes.declaraConstante ( identificador );		
	}

	private void parte_tipos ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		do
		{
			definicao_tipos ( no );
			no = no.irmao;			
		}		
		while ( no != null );
	}

	private void definicao_tipos ( ArvorePrograma no ) throws Exception
	{		
		//pego o nome da delcaracao
		no = no.filho;
		String identificador = no.valor;
		
		//pego o tipo
		no = no.irmao;
		Tipo tipo = tipo ( no );
		
		declaracoes.declaraTipo ( tipo, identificador );
	}

	private Tipo tipo (ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		//é de algum tipo jah existente
		if ( no.nomeNo.compareTo("identificador") == 0 )
		{
			Tipo tipo = declaracoes.pegaTipo ( no.valor );
			
			if ( tipo == null )
				throw new Exception ("Tipo não declarado.");
			
			return tipo;
		}
			
		//é um novo array
		if ( no.nomeNo.compareTo("tipo_array") == 0 )
			return tipo_array( no );
		
		//é um record!
		return tipo_registro( no );
	}

	private Tipo tipo_array (ArvorePrograma no ) throws Exception
	{
		//pego o tipo do array
		no = no.filho;
		Tipo tipo = tipo ( no );
		
		//instancio o tipo
		TipoArray tipoArray = new TipoArray ( tipo );
		
		//pego os indices
		no = no.irmao;
		do
		{
			//pego os ranges
			String inicioRange = no.filho.valor;
			String fimRange = no.filho.irmao.valor;
			
			//adiciono o indice para o array
			tipoArray.adicionaIndice ( inicioRange, fimRange );
			
			//caminho para o proximo indicec
			no = no.irmao;
		}
		while ( no != null) ;
		
		return tipoArray;
	}

	private Tipo tipo_registro (ArvorePrograma no ) throws Exception
	{
		//vou para o primeiro campo
		no = no.filho.filho;
		
		TipoRecord registro = new TipoRecord ( ); 
		
		do
		{
			//pego o tipo das propriedades
			Tipo tipoPropriedades = tipo ( no.filho.irmao );
			
			//adiciono propriedades
			ArvorePrograma identificador = no.filho.filho;
			while ( identificador != null )
			{
				if ( !registro.adicionaPropriedade ( tipoPropriedades, identificador.valor ) )
					throw new Exception ("Registro com propriedades com msm nome");
			
				//proxima propriedade
				no = no.irmao;
			}
			
			//vou para a proxima lista de propiedades
			no = no.irmao;
		}
		while ( no != null );
		
		return registro;
	}

	private void parte_vars ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		do 
		{		
			decl_vars ( no );
			no = no.irmao;
		} 
		while ( no != null );
	}

	private void decl_vars ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		//pego o tipo das propriedades
		Tipo tipoVariaveis = tipo ( no.irmao );
		
		//nomes das variaveis
		ArvorePrograma identificador = no.filho;
		while ( identificador != null )
		{
			if ( !declaracoes.declara(tipoVariaveis, identificador.valor) )
				throw new Exception ("Variavel com nome ja declarado!");
		
			//proxima variavel
			no = no.irmao;
		}		
	}

	private void parte_rotinas ( ArvorePrograma no ) throws Exception
	{				
		no = no.filho;
		
		do
		{
			if ( no.valor.compareTo( "declaracao_procedimento" ) == 0 )
				declaracao_procedimento ( no );
			else
				declaracao_funcao ( no );
			
			no = no.irmao;
		
		}while ( no != null );	
	}

	private void declaracao_procedimento (ArvorePrograma no ) throws Exception
	{		
		Tipo procedimento = new TipoProcedimento ( );
	
		//vou para o nome do procedimento
		no = no.filho;
		
		if ( !declaracoes.declaraProcedimento (procedimento, no.valor) )
			throw new Exception ("Nome de procedimento invalido: Identificador ja declarado");
		
		//verifico se tem parametros 
		no = no.irmao;
		if ( no.nomeNo.compareTo("bloco") != 0)
		{
			preencheParametros(no, procedimento);
		}
		
		bloco ( no );		
	}
	

	private void declaracao_funcao ( ArvorePrograma no ) throws Exception
	{		
		//pego o tipo do retorno
		no = no.filho;
		Tipo tipoRetorno = declaracoes.pegaTipo(no.valor);
		if ( tipoRetorno == null )
			throw new Exception ("Tipo não declarado");
	
		//instancio a funcao
		Tipo funcao = new TipoFuncao ( tipoRetorno );
		
		//vou para o nome da funcao
		no = no.irmao;
		
		//declaro a vaca
		if ( !declaracoes.declaraFuncao (funcao, no.valor) )
			throw new Exception ("Nome de procedimento invalido: Identificador ja declarado");
		
		//verifico se tem parametros 
		no = no.irmao;
		if ( no.nomeNo.compareTo("bloco") != 0)
		{
			preencheParametros(no, funcao);
		}
		
		bloco ( no );
	}

	private void preencheParametros(ArvorePrograma no, Tipo procedimento) throws Exception {
		//vou para os pares
		ArvorePrograma parFormal = no.filho;
		while ( parFormal != null )
		{
			//pego o tipo dos parametros
			Tipo tipoParametros = declaracoes.pegaTipo(no.filho.irmao.valor);
			if ( tipoParametros == null )
				throw new Exception ("Tipo não declarado");
			
			//vou para os identificadores
			ArvorePrograma identificador = parFormal.filho;
			while ( identificador != null )
			{
				//vou para os pares
				ArvorePrograma par = identificador.filho;
				while ( par != null )
				{
					//cheguei aos identificadores
					par = par.filho;
					
					//cheko se é por referencia
					boolean referencia = false;
					
					//adiciono na lista de parametros
					if ( !procedimento.adicionaParametro ( tipoParametros, par.valor, referencia ) )
						throw new Exception ("Parametro com nome duplicado");
					
					par = par.irmao;
				}
				identificador = identificador.irmao;
			}
			
			parFormal = parFormal.irmao;
		}
	}

	private void comando_composto ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		do
		{
			comando ( no );
			no = no.irmao;
		}
		while ( no.irmao != null );
				
	}
	
	/**
	 * 
	 * @param no
	 * @throws Exception
	 * @todo implementar skema de label
	 */
	private void comando ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		if ( no.nomeNo == "numero" )
		{
			/*
			if ( !declaracoes.estaDeclaradoNoEscopo ( no.valor, escopoAtual ) )
				throw new Exception ( "Nao foi declarado o rotulo" );
			no = no.irmao;
			*/
		}
		
		no = no.irmao;
		comando_sem_rotulo( no );		
	}

	private void comando_sem_rotulo ( ArvorePrograma no ) throws Exception
	{		
		no = no.filho;
		
		if ( no.nomeNo.compareTo("comando_sem_rotulo_identificador") == 0 )
			comando_sem_rotulo_identificador ( no );
					
		else if ( no.nomeNo.compareTo("desvio") == 0 )
			desvio ( no );

		else if ( no.nomeNo.compareTo("comando_composto") == 0 )
			comando_composto ( no );

		else if ( no.nomeNo.compareTo("comando_condicional") == 0 )
			comando_condicional ( no );

		else if ( no.nomeNo.compareTo("comando_for") == 0 )
			comando_for ( no );

		else if ( no.nomeNo.compareTo("comando_while") == 0 )
			comando_while ( no );
	}

	private void comando_sem_rotulo_identificador (ArvorePrograma no ) throws Exception
	{
		//vou para o identificador
		no = no.filho;		
		Tipo tipoIdentificador = declaracoes.pegaDeclaracao(no.valor);
		
		//verifico se foi declarado
		if (tipoIdentificador == null)
			throw new Exception ("Identificador não declarado");
		
		no = no.irmao;
		
		//checo compatibilade de tipos na expressao
		if ( no.nomeNo.compareTo("expressao") == 0 )
		{
			Tipo tipoExpressao = expressao(no);
			
			if ( !tipoIdentificador.verificaIgualdade(tipoExpressao) )
				throw new Exception ("Incompatibilidade de tipos");
			
			return; 
		}
		
		//parametros do procedimento
		if ( no.nomeNo.compareTo("lst_expressoes") == 0)
		{
			//o identificador é um procedimento?
			if ( !tipoIdentificador.verificaIgualdade("proc") )
				throw new Exception ("Tentativa de chamar um procedimento que não existe!");
		
			validaParametros ( no, (TipoProcedimento)tipoIdentificador );
			
			return;
		}
		
		//record ou array
		Tipo tipoInterno = variavel_parametros(tipoIdentificador, no);
		
		Tipo tipoExpressao = expressao(no);
		
		if ( !tipoInterno.verificaIgualdade(tipoExpressao) )
			throw new Exception ("Incompatibilidade de tipos");
				
	}
	
	private Tipo variavel ( ArvorePrograma no ) throws Exception
	{
		//pego o nome
		no = no.filho;	
		Tipo tipoVariavel = declaracoes.pegaDeclaracao(no.valor);
		
		//checo declaracao
		if ( tipoVariavel == null )
			throw new Exception ("Variavel não declarada");
		
		no = no.irmao;
		
		//é uma simples variavel?
		if ( no == null )			
			return tipoVariavel;
		
		//eh um record ou um array		
		return variavel_parametros(tipoVariavel, no);
	}

	
	private void desvio ( ArvorePrograma no ) throws Exception
	{
		
	}

	private void comando_condicional ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		//expressao deve ser booleana!
		if ( !expressao ( no ).verificaIgualdade ( "bool" ) )
			throw new Exception ("Experado bool na condição do comando if"); 

		//then
		no = no.irmao;		
		comando_sem_rotulo ( no );		
		
		//else
		no = no.irmao;
		if ( no != null )
			comando_sem_rotulo ( no );
	}

	private void comando_for (ArvorePrograma no ) throws Exception
	{
		//pego a variavel de controle
		no = no.filho;
		Tipo tipoVariavel = variavel(no);
		
		//o tipo deve ser inteiro
		if ( !tipoVariavel.verificaIgualdade("integer") )
			throw new Exception ("Variavel de controle do bloco IF deve ser inteira");
		
		//de
		no = no.irmao;		
		if ( !expressao(no).verificaIgualdade("integer") )
			throw new Exception ("Expressao do bloco IF deve ser inteira");
		
		//ate
		no = no.irmao;
		if ( !expressao(no).verificaIgualdade("integer") )
			throw new Exception ("Expressao do bloco IF deve ser inteira");
		
		no = no.irmao;
		comando_sem_rotulo(no);
	}

	private void comando_while ( ArvorePrograma no ) throws Exception 
	{		
		no = no.filho;
		
		//expressao deve ser booleana!
		if ( !expressao ( no ).verificaIgualdade ( new Tipo ("bool") ) )
			throw new Exception ("Experado bool na condição do comando while"); 

		//vou para o comando sem rotulo
		no = no.irmao;		
		comando_sem_rotulo ( no );		
	}

	private Tipo expressao ( ArvorePrograma no ) throws Exception
	{			
		no = no.filho;
		
		//pego o tipo da primeira expressao
		Tipo tipoPrimeiraExpressao = expressao_simples ( no );
		no = no.irmao; 
				
		//se não possui relacao
		if ( no == null )
			//retorno o tipo da primeira
			return tipoPrimeiraExpressao;
		
		//pego o operador da relacao
		String relacao = relacao ( no );
		
		//verifico tipo da relacao e tipo da primeira expressao
		if ( tipoPrimeiraExpressao.verificaIgualdade( new Tipo ("bool") ) &&			
			 relacao.compareTo("OP_IGUAL") != 0 && relacao.compareTo("OP_DIFERE") != 0 )			
			throw new Exception ( "Relação invalida" );

		//vou para proxima expressao
		no = no.irmao;
		Tipo tipoSegundaExpressao = expressao_simples ( no );
		
		//verifico se são do msm tipo
		if ( !tipoPrimeiraExpressao.verificaIgualdade(tipoSegundaExpressao) )
			throw new Exception ( "Relação invalida: Incompatibilidade de tipos" );
		
		return tipoPrimeiraExpressao;
	}

	private String relacao ( ArvorePrograma no ) throws Exception
	{
		return no.valor;
	}

	private Tipo expressao_simples ( ArvorePrograma no ) throws Exception
	{
		Tipo tipo, tipoAnterior = null;
		
		no = no.filho;		
		
		do
		{			
			//se tem operador
			if ( no.nomeNo.compareTo("operador") == 0)
			{
				//pego tipo operador
				if ( no.valor.compareTo("PA_OR") == 0)
					tipo = new Tipo ("bool");
				else
					tipo = new Tipo ("integer");
				
				//verifico o tipo
				if ( tipoAnterior != null && !tipo.verificaIgualdade(tipoAnterior) )
					throw new Exception ("Incompatibilidade de tipos.");
			
				//passo o tipo para o anterior
				tipoAnterior = tipo;			
				
				//vou para o termo
				no = no.irmao;
			}
						
			//pego o tipo do termo
			tipo = termo ( no );			
			
			//verifico o tipo
			if ( tipoAnterior != null && !tipo.verificaIgualdade(tipoAnterior) )
				throw new Exception ("Incompatibilidade de tipos.");
			
			//passo o tipo para o anterior
			tipoAnterior = tipo;
			
			//caminha na arvore
			no = no.irmao;
		}
		while ( no != null );
		
		return tipo;
	}

	private Tipo termo ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		Tipo tipo, tipoAnterior = null;
		
		do
		{							
			//pego o tipo do fator
			tipo = fator ( no );
			
			//verifico o tipo
			if ( tipoAnterior != null && !tipo.verificaIgualdade(tipoAnterior) )
				throw new Exception ("Incompatibilidade de tipos.");
			
			tipoAnterior = tipo;
			//caminha na arvore
			no = no.irmao;
			
			//se tem operador
			if ( no!= null)
			{
				//pego tipo operador
				if ( no.valor.compareTo("PA_AND") == 0)
					tipo = new Tipo ("bool");
				else
					tipo = new Tipo ("integer");
				
				//verifico o tipo
				if ( !tipo.verificaIgualdade(tipoAnterior) )
					throw new Exception ("Incompatibilidade de tipos.");
				
				//vou para o termo
				no = no.irmao;
			}
		}
		while ( no != null );
		
		return tipo;		
	}

	private Tipo fator ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		if ( no.nomeNo.compareTo( "fator_identificador" ) == 0 )
			return fator_identificador ( no );
	
		if ( no.nomeNo.compareTo( "expressao" ) == 0 )
			return expressao ( no );
		
		if ( no.nomeNo.compareTo( "numero" ) == 0 )
			return new Tipo ("integer");
		
		Tipo tipo = fator ( no );
		
		if ( !tipo.verificaIgualdade( new Tipo ("bool") ) )
			throw new Exception ( "Incompatibilidade de tipos." );
		
		return tipo;	
	}
	
	private Tipo fator_identificador ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		//pego nome da chamada
		String identificador = no.valor; 
		
		no = no.irmao;
		
		//é uma varivel
		if ( no == null )			
			return declaracoes.pegaTipo ( identificador );
		
		//é uma funcao
		if ( no.nomeNo.compareTo("lst_expressoes") == 0 )			
			return validaFuncao ( identificador, no );
				
		//pego a declaracao
		Tipo tipo = declaracoes.pegaDeclaracao (identificador);
		
		//verifico declaracao
		if ( tipo == null )
			throw new Exception ("Identificador não declarado!");
		
		return variavel_parametros (tipo, no);
	}

	private Tipo variavel_parametros (Tipo tipo, ArvorePrograma no) throws Exception
	{
		//vou para o filho
		no = no.filho;		
		
		//é um array
		if ( no.nomeNo.compareTo("lst_expressoes") == 0 )
			return validaArray ( (TipoArray)tipo, no );
		
		//é um record
		return validaRecord ( (TipoRecord)tipo, no );
	}
	
	private Tipo validaRecord(TipoRecord tipo, ArvorePrograma no) throws Exception {
					
		Tipo tipoPropriedade = tipo.pegaTipoPropriedade ( no.valor );
		
		if ( tipoPropriedade == null )
			throw new Exception ("Propriedade invalida para o record");
		
		//eh um record recursivo
		if ( no.irmao != null )
			return variavel_parametros(tipo, no);
			
		return tipo;
	}

	private Tipo validaArray(TipoArray tipo, ArvorePrograma no) throws Exception {

		Tipo tipoExpressao;
		int contadorDimensoes = 1;
				
		//vou para a primeira expressao
		no = no.filho;
		
		do
		{
			//verifico se estourou o numero de dimensoes
			if ( contadorDimensoes++ > tipo.pegaNumeroDimensoes ( ) )
				throw new Exception ("Dimensao invalida do array");
			
			//pego o tipo da expressao
			tipoExpressao = expressao(no);
			
			//verifico se a expressao indice é inteira
			if ( !tipoExpressao.verificaIgualdade( new Tipo ("integer") ) )
				throw new Exception ("Indexando array com tipo não inteiro");
						
			no = no.irmao;			
		}
		while ( no != null );
		
		//verifico por recursao
		if ( no.pai.irmao != null )
			return variavel_parametros(tipo, no.pai.irmao);
		
		return tipo;
	}

	private Tipo validaFuncao(String identificador, ArvorePrograma no) throws Exception {
				
		TipoFuncao funcao = declaracoes.pegaFuncao ( identificador );
		
		if ( funcao == null )
			throw new Exception ("Chamada a função não declarada" );
		
		validaParametros(no, funcao);
		
		return funcao;
	}

	private void validaParametros(ArvorePrograma no, TipoProcedimento procedimento) throws Exception {

		int i = 0;
		
		//vou para o primeiro parametro
		no = no.filho;

		while ( no != null )
		{
			//verifico tipo do parametro
			if ( procedimento.pegaTipoParametro ( i++ ).compareTo ( expressao(no) ) != 0 )
				throw new Exception ("Tipo de parametro incompativel em chamada de função");
			
			no = no.irmao;
		}
	}
}
