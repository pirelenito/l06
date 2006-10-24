package Semantico;

import Sintatico.ArvorePrograma;

public class AnalizadorSemantico {
	
	private Declaracoes declaracoes;
	
	public AnalizadorSemantico()
	{
		declaracoes = new Declaracoes();
	}
			
	public void validaArvore(ArvorePrograma arvorePrograma) throws Exception{		
	
		programa ( arvorePrograma );		
	}
		
	private void programa ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		///declaracoes.declara ( no.valor, no.pai, escopoAtual, new Tipo ( Tipo.TIPO_PROGRAMA ) );

		no = no.irmao;

		declaracoes.sobeEscopo ( );
		
		bloco ( no );
	}
	
	private void bloco ( ArvorePrograma no ) throws Exception
	{			
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
			Declaracao rotulo = new Rotulo ( no.valor );
			declaracoes.declara( rotulo );
			
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
		Variavel valor = expressao(no);
		if ( !valor.constante )
			throw new Exception ( "Tentando instanciar uma constante com valores não constantes" );
		
		Declaracao variavel = new Variavel ( identificador, valor.tipo, true, false );
		
		declaracoes.declara ( variavel );
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
		
		Tipo declaracao = new TipoDefinido ( tipo, identificador );
		
		declaracoes.declara ( declaracao );
	}

	private Tipo tipo (ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		//é de algum tipo jah existente
		if ( no.nomeNo.compareTo("identificador") == 0 )
		{
			Tipo tipo = (Tipo)declaracoes.pegaDeclaracao( no.valor );
			
			if ( !confirmaTipo(tipo) )
				throw new Exception ("Esperado tipo. Encontrado " + tipo.pegaComportamento() );
			
			return tipo;
		}
			
		//é um novo array
		if ( no.nomeNo.compareTo("tipo_array") == 0 )
			return tipo_array( no );
		
		//é um record!
		return tipo_registro( no );
	}

	private boolean confirmaTipo(Tipo tipo) {
		return tipo.pegaComportamento().compareTo("Tipo") == 0 ||
				tipo.pegaComportamento().compareTo("TipoArray") == 0 ||
				tipo.pegaComportamento().compareTo("TipoRecord") == 0 ||
				tipo.pegaComportamento().compareTo("TipoDefinido") == 0;
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
			
			//caminho para o proximo indice
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
				registro.adicionaPropriedade ( tipoPropriedades, identificador.valor );
			
				//proxima propriedade
				identificador = identificador.irmao;
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
		
		Variavel variavel;
		
		//nomes das variaveis
		ArvorePrograma identificador = no.filho;
		while ( identificador != null )
		{
			variavel = new Variavel ( identificador.valor, tipoVariaveis, false, false );
			
			declaracoes.declara( variavel );
		
			//proxima variavel
			identificador = identificador.irmao;
		}		
	}

	private void parte_rotinas ( ArvorePrograma no ) throws Exception
	{				
		no = no.filho;
		
		do
		{
			if ( no.nomeNo.compareTo( "declaracao_procedimento" ) == 0 )
				declaracao_procedimento ( no );
			else
				declaracao_funcao ( no );
			
			no = no.irmao;
		
		}while ( no != null );	
	}

	private void declaracao_procedimento (ArvorePrograma no ) throws Exception
	{		
		//vou para o nome do procedimento
		no = no.filho;
		
		Procedimento procedimento = new Procedimento ( no.valor );
		
		declaracoes.declara ( procedimento );
		
		declaracoes.sobeEscopo ( );
		
		//verifico se tem parametros 
		no = no.irmao;
		if ( no.nomeNo.compareTo("bloco") != 0)
		{
			preencheParametros(no, procedimento);
			no = no.irmao;
		}
		
		bloco ( no );		
	}
	

	private void declaracao_funcao ( ArvorePrograma no ) throws Exception
	{		
		//pego o tipo do retorno
		no = no.filho;
		Tipo tipoRetorno = (Tipo)declaracoes.pegaDeclaracao(no.valor);
		if ( !confirmaTipo(tipoRetorno) )
			throw new Exception ("Tipo não declarado");
			
		//vou para o nome da funcao
		no = no.irmao;
		
		//instancio a funcao
		Funcao funcao = new Funcao ( tipoRetorno, no.valor );
		
		//declaro a vaca
		declaracoes.declara ( funcao );
		
		declaracoes.sobeEscopo ( );
		
		//verifico se tem parametros 
		no = no.irmao;
		if ( no.nomeNo.compareTo("bloco") != 0)
		{
			preencheParametros(no, funcao);
			no = no.irmao;
		}
		
		bloco ( no );
	}

	private void preencheParametros(ArvorePrograma no, Procedimento procedimento) throws Exception {
		
		//vou para os pares
		ArvorePrograma parFormal = no.filho;
		while ( parFormal != null )
		{
			//pego o tipo dos parametros
			Tipo tipoParametros = (Tipo)declaracoes.pegaDeclaracao(parFormal.filho.irmao.valor);
			if ( !confirmaTipo(tipoParametros) )
				throw new Exception ("Tipo não declarado");
			
			//vou para as listas de identificadores
			ArvorePrograma listaIdentificador = parFormal.filho;
			while ( listaIdentificador.irmao != null )
			{
				//vou para os pares
				ArvorePrograma par = listaIdentificador.filho;
				while ( par != null )
				{
					boolean referencia = false;
					
					//cheguei aos identificadores
					ArvorePrograma arvoreTemp = par.filho;
					
					//cheko se é por referencia
					if ( arvoreTemp.nomeNo.compareTo("operador") == 0 )
					{
						referencia = true;
						par = par.irmao;
					}
										
					//crio uma variavel
					Variavel parametro = new Variavel ( arvoreTemp.valor, tipoParametros, false, referencia );
					
					//adiciono na lista de parametros
					procedimento.adicionaParametro ( parametro );
					
					//coloco no escopo
					declaracoes.declara( parametro );
											
					par = par.irmao;
				}
				listaIdentificador = listaIdentificador.irmao;
			}
			
			parFormal = parFormal.irmao;
		}
	}

	private void comando_composto ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		while ( no != null )
		{
			comando ( no );
			no = no.irmao;
		}
				
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
			no = no.irmao;
		}
		
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
		Declaracao declaracao = declaracoes.pegaDeclaracao(no.valor);
		
		no = no.irmao;
		
		//checo compatibilade de tipos na expressao
		if ( no.nomeNo.compareTo("expressao") == 0 )
		{
			//faço casting e checo comportamento 
			Variavel primeiraVariavel = (Variavel)declaracao;
			if ( primeiraVariavel.pegaComportamento().compareTo("Variavel") != 0 )
				throw new Exception ("Esperado variavel");
			
			//pego segunda variavel
			Variavel segundaVariavel = expressao(no);
			
			//faço a atribuiçao
			primeiraVariavel.atribui ( segundaVariavel );
						
			return; 
		}
		
		//parametros do procedimento
		if ( no.nomeNo.compareTo("lst_expressoes") == 0)
		{
			//faço casting e checo comportamento 
			Procedimento procedimento = (Procedimento)declaracao;
			if ( procedimento.pegaComportamento().compareTo("Procedimento") != 0 )
				throw new Exception ("Esperado procedimento");
			
			validaParametros ( no, procedimento );
			
			return;
		}
		
		//faço casting e checo comportamento 
		Variavel variavel = (Variavel)declaracao;
		if ( variavel.pegaComportamento().compareTo("Variavel") != 0 )
			throw new Exception ("Esperado variavel");
		
		//record ou array
		//pego o tipo interno (propriedade de um record ou o tipo de um indice do array)
		Tipo tipoInterno = variavel_parametros(variavel.tipo, no);
		
		//pego a expressao de origem dos dados
		Variavel expressao = expressao(no);
		
		//crio uma variavel temporaria com o tipo interno para realizar a atribuicao
		Variavel tempAtribuicao = new Variavel ( tipoInterno, variavel.constante, variavel.referencia );
		
		tempAtribuicao.atribui(expressao);				
	}
	
	private Variavel variavel ( ArvorePrograma no ) throws Exception
	{
		//pego o nome
		no = no.filho;	
		Variavel variavel = (Variavel)declaracoes.pegaDeclaracao(no.valor);
		if ( variavel.pegaComportamento().compareTo("Variavel") != 0 )
			throw new Exception ( "Esperado variavel" );
				
		no = no.irmao;
		
		//é uma simples variavel?
		if ( no == null )			
			return variavel;
		
		//eh um record ou um array		
		return new Variavel ( variavel_parametros(variavel.tipo, no), variavel.constante, variavel.referencia );
	}

	
	private void desvio ( ArvorePrograma no ) throws Exception
	{
		
	}

	private void comando_condicional ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		//expressao deve ser booleana!
		if ( !expressao ( no ).tipo.verificaIgualdade ( Tipo.tipoBooleano() ) )
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
		Variavel variavel = variavel(no);
		
		//o tipo deve ser inteiro
		if ( !variavel.tipo.verificaIgualdade( Tipo.tipoInteiro() ) )
			throw new Exception ("Variavel de controle do bloco IF deve ser inteira");
		
		//de
		no = no.irmao;		
		if ( !expressao(no).tipo.verificaIgualdade( Tipo.tipoInteiro() ) )
			throw new Exception ("Expressao do bloco IF deve ser inteira");
		
		//ate
		no = no.irmao;
		if ( !expressao(no).tipo.verificaIgualdade( Tipo.tipoInteiro() ) )
			throw new Exception ("Expressao do bloco IF deve ser inteira");
		
		no = no.irmao;
		comando_sem_rotulo(no);
	}

	private void comando_while ( ArvorePrograma no ) throws Exception 
	{		
		no = no.filho;
		
		//expressao deve ser booleana!
		if ( !expressao ( no ).tipo.verificaIgualdade( Tipo.tipoBooleano() ) )
			throw new Exception ("Experado bool na condição do comando while"); 

		//vou para o comando sem rotulo
		no = no.irmao;		
		comando_sem_rotulo ( no );		
	}

	private Variavel expressao ( ArvorePrograma no ) throws Exception
	{					
		no = no.filho;
		
		//pego o tipo da primeira expressao
		Variavel primeiraExpressao = expressao_simples ( no );
		no = no.irmao; 
				
		//se não possui relacao
		if ( no == null )
			//retorno primeira variavel
			return primeiraExpressao;
		
		//pego o operador da relacao
		String relacao = relacao ( no );
		
		//verifico tipo da relacao e tipo da primeira expressao
		if ( primeiraExpressao.tipo.verificaIgualdade( Tipo.tipoBooleano() ) &&			
			 relacao.compareTo("OP_IGUAL") != 0 && relacao.compareTo("OP_DIFERE") != 0 )			
			throw new Exception ( "Relação invalida" );
		
		//vou para proxima expressao
		no = no.irmao;
		Variavel segundaExpressao = expressao_simples ( no );
		
		//verifico se são do msm tipo
		return primeiraExpressao.opera ( segundaExpressao );		
	}

	private String relacao ( ArvorePrograma no ) throws Exception
	{
		return no.valor;
	}

	private Variavel expressao_simples ( ArvorePrograma no ) throws Exception
	{
		Variavel variavel = null, variavelAnterior = null;
		
		Tipo operador = null; 
		
		no = no.filho;		
		
		do
		{			
			//se tem operador
			if ( no.nomeNo.compareTo("operador") == 0)
			{
				//pego tipo operador
				if ( no.valor.compareTo("PA_OR") == 0)
					operador = Tipo.tipoBooleano();
				else
					operador = Tipo.tipoInteiro();
				
				//verifico o tipo
				if ( variavel != null && !variavel.tipo.verificaIgualdade(operador) )
					throw new Exception ("Incompatibilidade de tipos.");
				
				//vou para o termo
				no = no.irmao;
			}
			
			variavelAnterior = variavel;
			
			//pego o termo
			variavel = termo ( no );			
			
			//se tem outra variavel
			if ( variavelAnterior != null )
				variavel = variavel.opera( variavelAnterior);	
			
			//caminha na arvore
			no = no.irmao;
		}
		while ( no != null );
		
		return variavel;
	}

	private Variavel termo ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		Variavel variavel = null, variavelAnterior = null;
		Tipo operador;
		
		do
		{	
			//libero espaco para proxima variavel
			variavelAnterior = variavel;
			
			//pego o fator
			variavel = fator ( no );
			
			//verifico o tipo
			if ( variavelAnterior != null )
				variavel = variavel.opera(variavelAnterior);
						
			//caminha na arvore
			no = no.irmao;
			
			//se tem operador
			if ( no!= null)
			{
				//pego tipo operador
				if ( no.valor.compareTo("PA_AND") == 0)
					operador = Tipo.tipoBooleano();
				else
					operador = Tipo.tipoInteiro();
				
				//verifico o tipo
				if ( !variavel.tipo.verificaIgualdade(operador) )
					throw new Exception ("Incompatibilidade de tipos.");
				
				//vou para o termo
				no = no.irmao;
			}
		}
		while ( no != null );
		
		return variavel;		
	}

	private Variavel fator ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		if ( no.nomeNo.compareTo( "fator_identificador" ) == 0 )
			return fator_identificador ( no );
	
		if ( no.nomeNo.compareTo( "expressao" ) == 0 )
			return expressao ( no );
		
		if ( no.nomeNo.compareTo( "numero" ) == 0 )
			return new Variavel ( Tipo.tipoInteiro(), true, false );
		
		Variavel variavel = fator ( no );
		
		if ( !variavel.tipo.verificaIgualdade( Tipo.tipoBooleano() ) )
			throw new Exception ( "Incompatibilidade de tipos." );
		
		return variavel;	
	}
	
	private Variavel fator_identificador ( ArvorePrograma no ) throws Exception
	{
		Variavel retorno;
		
		no = no.filho;
		
		//pego nome da chamada
		String identificador = no.valor; 
		
		no = no.irmao;
		
		//é uma variavel simples
		if ( no == null )
		{
			retorno = (Variavel)declaracoes.pegaDeclaracao ( identificador );
			if ( retorno.pegaComportamento().compareTo("Variavel") != 0 )
				throw new Exception ( "Esperado uma variavel!" ); 
			
			return retorno;
		}
		
		//é uma funcao
		if ( no.nomeNo.compareTo("lst_expressoes") == 0 )
			return new Variavel ( validaFuncao ( identificador, no ), false, false );
				
		//pego a declaracao
		Variavel variavelTemp = (Variavel)declaracoes.pegaDeclaracao (identificador);
				
		//eh um registro ou array
		return new Variavel ( variavel_parametros (variavelTemp.tipo, no), variavelTemp.constante, variavelTemp.referencia );
	}

	private Tipo variavel_parametros (Tipo tipo, ArvorePrograma no) throws Exception
	{
		//vou para o filho
		no = no.filho;		
		
		//é um array
		if ( no.nomeNo.compareTo("lst_expressoes") == 0 )
			return validaArray ( (TipoArray)tipo, no );
		
		//é um record (passo o tipo para validacao))
		return validaRecord ( (TipoRecord)tipo, no );
	}
	
	private Tipo validaRecord(TipoRecord tipo, ArvorePrograma no) throws Exception {
					
		if ( tipo.pegaComportamento().compareTo("TipoRecord") != 0 )
			throw new Exception ( "Esperado registro" );
		
		Tipo tipoPropriedade = tipo.pegaTipoPropriedade ( no.valor );
		
		if ( tipoPropriedade == null )
			throw new Exception ("Propriedade invalida para o record");
		
		//eh um record recursivo
		if ( no.irmao != null )
			return variavel_parametros( tipoPropriedade, no);
			
		return tipo;
	}

	private Tipo validaArray(TipoArray tipo, ArvorePrograma no) throws Exception {

		if ( tipo.pegaComportamento().compareTo("TipoArray") != 0 )
			throw new Exception ( "Esperado array" );
		
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
			tipoExpressao = expressao(no).tipo;
			
			//verifico se a expressao indice é inteira
			if ( !tipoExpressao.verificaIgualdade( Tipo.tipoInteiro() ) )
				throw new Exception ("Indexando array com tipo não inteiro");
						
			no = no.irmao;			
		}
		while ( no != null );
		
		//verifico por recursao
		if ( no.pai.irmao != null )
			return variavel_parametros(tipo.tipoDados, no.pai.irmao);
		
		return tipo;
	}

	private Tipo validaFuncao(String identificador, ArvorePrograma no) throws Exception {
		
		Funcao declaracao = (Funcao)declaracoes.pegaDeclaracao ( identificador );
		if ( declaracao.pegaComportamento().compareTo("Funcao") != 0 )
			throw new Exception ( "Esperado uma funcao!" );
				
		validaParametros(no, declaracao);
		
		return declaracao.retorno;
	}

	private void validaParametros(ArvorePrograma no, Procedimento procedimento) throws Exception {

		int i = 0;
		Variavel parametro, variavel;
		//vou para o primeiro parametro
		no = no.filho;
		
		while ( no != null )
		{
			parametro = procedimento.pegaParametro(i++); 
						
			variavel = expressao (no);
			
			//verifico tipo do parametro
			if ( parametro.referencia )
				if (variavel.identificador.length() == 0 || variavel.constante )
					throw new Exception ("Tipo de parametro incompativel. Parametro e uma refrencia.");
			
			parametro.atribui(variavel);
			
			no = no.irmao;
		}
	}
}
