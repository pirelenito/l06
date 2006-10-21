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
		declaracoes.declara ( no.valor, no.pai, escopoAtual, new Tipo ( Tipo.TIPO_PROGRAMA ) );

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
			if ( !declaracoes.declara(no.valor, new Tipo ( Tipo.TIPO_ROTULO ), no.pai ) )
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

	private void decl_cte ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "decl_cte" );
		
		validaToken( "TO_ID", 0 );
		
		leioToken ( );
		validaToken ( "PA_IS", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( expressao ( ) );

		return eu;
	}

	private void parte_tipos ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		do
		{
			dedfinicao_tipo ( no );
			no = no.irmao;
			
		}
		
		while ( no != null );

	}

	private void definicao_tipos ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "definicao_tipos" );
		
		identificador();
				
		leioToken ( );
		validaToken ( "OP_IGUAL", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( tipo ( ) );

		return eu;
	}

	private void tipo ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "tipo" );
		
		if ( comparaToken ( "TO_ID", 0 ) )
			eu.adicionaFilho ( identificador ( ) );

		else if ( comparaToken ( "PA_ARRAY", 1 ) )
			eu.adicionaFilho ( tipo_array ( ) );

		else if ( comparaToken ( "PA_RECORD", 1 ) )
			eu.adicionaFilho ( tipo_registro ( ) );
		else
			throw geraErro ( "tipo", token[1] );

		return eu;
	}

	private void tipo_array ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "tipo_array" );
		
		validaToken ( "PA_ARRAY" , 1 );

		leioToken ( );
		validaToken ( "OP_ABR_PAR", 1 );

		leioToken ( );
		eu.adicionaFilho ( tipo ( ) );

		leioToken ( );
		validaToken ( "OP_FCH_PAR", 1 );

		leioToken ( );
		validaToken ( "OP_ABR_COLC", 1 );

		do
		{		
			leioToken ( );
			eu.adicionaFilho ( indice ( ) );
		
			leioToken ( );
		}
		while ( comparaToken ( "OP_VIRG", 1 ) );
		
		validaToken ( "OP_FCH_COLC", 1 );		

		return eu;
	}

	private void indice ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "indice" );
		
		eu.adicionaFilho ( numero ( ) );	

		leioToken ( );
		validaToken ( "OP_PTOPTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( numero ( ) );	

		return eu;
	}

	private void tipo_registro ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "tipo_registro" );
		
		validaToken ( "PA_RECORD", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( lst_campo ( ) );

		leioToken ( );
		validaToken ( "PA_END", 1 );
		
		leioToken ( );
		validaToken ( "PA_RECORD", 1 );
		
		return eu;
	}


	private void lst_campo ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "lst_campo" );
		
		eu.adicionaFilho ( campo ( ) );

		leioToken ( );
		while ( comparaToken ( "OP_PTVG", 1 ) )
		{
			leioToken ( );

			eu.adicionaFilho ( campo ( ) );

			leioToken ( );
		}
		
		return eu;
	}

	private void campo ( ) throws Exception
	{	
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "campo" );
		
		eu.adicionaFilho ( lst_identificadores ( ) );
		
		leioToken ( );
		validaToken ( "OP_2PTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( tipo ( ) );
		
		return eu;
	}

	private void lst_identificadores ( ) throws Exception
	{	
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "lst_identificadores" );
		
		eu.adicionaFilho ( identificador ( ) );

		leioToken ( );
		while ( comparaToken ( "OP_VIRG", 1 ) )
		{
			leioToken ( );

			eu.adicionaFilho ( identificador ( ) );

			leioToken ( );
		}
		
		return eu;
	}

	private void parte_vars ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		do 
		{		
			dedcl_vars ( no );
			no = no.irmao;
		} 
		while ( no != null );
						
		return eu;
	}

	private void decl_vars ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "decl_vars" );
		
		eu.adicionaFilho ( lst_identificadores ( ) );
		
		leioToken ( );
		validaToken ( "OP_2PTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( tipo ( ) );	
		
		return eu;
	}

	private void parte_rotinas ( ArvorePrograma no ) throws Exception
	{		
		
		no = no.filho;
		
		do
		{
			if ( no.valor.compareTo( "declaracao_procedimento" ) == 0 )
				declaracao_proceddimento ( no );
			else
				declaracao_funcao ( no );
			
			no = no.irmao;
		}
		while ( no != null );
		
		
	}

	private void declaracao_procedimento ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "declaracao_procedimento" );
		validaToken ( "PA_PROC", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( identificador ( ) );

		leioToken ( );
		if ( comparaToken ( "OP_ABR_PAR", 1 ) ) 
			eu.adicionaFilho ( parametros_formais ( ) );

		leioToken ( );
		validaToken ( "PA_IS", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( bloco ( ) );

		leioToken ( );
		validaToken ( "OP_PTVG", 1 );
		
		return eu;
	}

	private void declaracao_funcao ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "declaracao_funcao" );
		
		validaToken ( "PA_FUNC", 1 );
		
		leioToken ( );
		validaToken ( "OP_ABR_COLC", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( identificador ( ) );

		leioToken ( );
		validaToken ( "OP_FCH_COLC", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( identificador ( ) );

		leioToken ( );
		if ( comparaToken ( "OP_ABR_PAR", 1 ) ) 
			eu.adicionaFilho ( parametros_formais ( ) );
		
		leioToken ( );
		validaToken ( "PA_IS", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( bloco ( ) );

		leioToken ( );
		validaToken ( "OP_PTVG", 1 );
				
		return eu;
	}

	private void parametros_formais ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parametros_formais" );
		
		validaToken ( "OP_ABR_PAR", 1 );
		
		do 
		{
			leioToken ( );
			eu.adicionaFilho ( par_formal ( ) );

			leioToken ( );
		}
		while ( comparaToken ( "OP_PTVG", 1 ) );
		
		validaToken ( "OP_FCH_PAR", 1 );
		
		return eu;
	}

	private void par_formal ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "par_formal" );
		
		eu.adicionaFilho ( lst_identificadores_par ( ) );

		leioToken ( );
		validaToken ( "OP_2PTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( identificador ( ) );
		
		return eu;
	}

	private void lst_identificadores_par ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "lst_identificadores_par" );
		
		eu.adicionaFilho ( par ( ) );

		leioToken ( );
		while ( comparaToken ( "OP_VIRG", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( par ( ) );

			leioToken ( );
		}
		
		return eu;
	}

	private void par ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "par" );
		
		if ( comparaToken ( "OP_EXCL", 1 ) )
			leioToken ( );

		eu.adicionaFilho ( identificador ( ) );
		
		return eu;
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

	private void comando ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		if ( no.nomeNo == "numero" )
		{
			if ( !declaracoes.estaDeclaradoNoEscopo ( no.valor, escopoAtual ) )
				throw new Exception ( "Nao foi declarado o rotulo" );
			no = no.irmao;
		}
		
		comando_sem_rotulo( no );
		
		
	}

	private void comando_sem_rotulo ( ArvorePrograma no ) throws Exception
	{		
		no = no.filho;
		
		if ( no.nomeNo == "comando_sem_rotulo_identificador" )
			comando_sem_rotulo_identificador ( no );
					
		else if ( no.nomeNo == "desvio" )
			desvio ( no );

		else if ( no.nomeNo == "comando_composto" )
			comando_composto ( no );

		else if ( no.nomeNo == "comando_condicional" )
			comando_condicional ( no );

		else if ( no.nomeNo == "comando_for" )
			comando_for ( no );

		else if ( no.nomeNo == "comando_while" )
			comando_while ( no );
	}

	private void  comando_sem_rotulo_identificador ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando_sem_rotulo_identificador" );
		
		identificador();
		
		leioToken ( );
		if ( comparaToken ( "OP_ATRIB", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( expressao ( ) );
		}
		else if ( comparaToken ( "OP_ABR_COLC", 1 ) || comparaToken ( "OP_PTO", 1 ) )
		{
			eu.adicionaFilho ( variavel_parametros( ) );
			
			leioToken ( );
			validaToken ( "OP_ATRIB", 1 );
			
			leioToken ( );
			eu.adicionaFilho ( expressao( ) ); 			
		}
		else if ( comparaToken ( "OP_ABR_PAR", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( lst_expressoes( ) ); 			
			
			leioToken ( );
			validaToken ( "OP_FCH_PAR", 1 );
		}
		
		return eu;
	}
	
	private void variavel ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "variavel" );
		
		identificador();
		
		leioToken ( );
		if ( comparaToken ( "OP_ABR_COLC", 1 ) || comparaToken ( "OP_PTO", 1 ) )
			eu.adicionaFilho ( variavel_parametros ( ) );
		
		return eu;
	}

	private void variavel_parametros ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "variavel_parametros" );
		
		if ( comparaToken ( "OP_ABR_COLC", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( lst_expressoes ( ) );
			
			leioToken ( );
			validaToken ( "OP_FCH_COLC", 1 );
		}			
		else if ( comparaToken ( "OP_PTO", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( identificador ( ) );
		}
		
		if ( comparaToken ( "OP_ABR_COLC", 1 ) || comparaToken ( "OP_PTO", 1 ) )
			eu.adicionaFilho ( variavel_parametros ( ) );
			
		return eu;
	}
	
	private void desvio ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "desvio" );
		
		validaToken ( "PA_GOTO", 1 );

		leioToken ( );
		eu.adicionaFilho ( numero ( ) );
		
		return eu;
	}

	private void comando_condicional ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando_condicional" );
		
		validaToken ( "PA_IF", 1 );

		leioToken ( );
		eu.adicionaFilho ( expressao ( ) );

		leioToken ( );
		validaToken ( "PA_THEN", 1 );

		leioToken ( );
		eu.adicionaFilho ( comando_sem_rotulo ( ) );

		leioToken ( );
		if ( comparaToken ( "PA_ELSE", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( comando_sem_rotulo ( ) );
		}

		leioToken ( );
		validaToken ( "PA_FI", 1 );
		
		return eu;
	}

	private void comando_for ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando_for" );
		
		validaToken ( "PA_FORALL", 1 );

		leioToken ( );
		eu.adicionaFilho ( variavel ( ) );
		
		leioToken ( );
		validaToken ( "PA_IN", 1 );

		leioToken ( );
		eu.adicionaFilho ( expressao ( ) );

		leioToken ( );
		validaToken ( "OP_PTOPTO", 1 );

		leioToken ( );
		eu.adicionaFilho ( expressao ( ) );

		leioToken( );
		validaToken ( "PA_DO", 1 );

		leioToken ( );
		eu.adicionaFilho ( comando_sem_rotulo ( ) );
		
		return eu;
	}

	private void comando_while ( ArvorePrograma no ) throws Exception 
	{		
		no = no.filho;
		
		if ( expressao ( no ).compareTo ( "bool" ) != 0 )
			throw new Exception ("Experado bool na condição do comando while"); 

		leioToken ( );
		validaToken ( "PA_DO", 1 );

		leioToken ( );
		eu.adicionaFilho ( comando_sem_rotulo ( ) );
		
		return eu;
	}

	private void lst_expressoes ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "lst_expressoes" );
		
		eu.adicionaFilho ( expressao ( ) );
		
		leioToken ( );
		while ( comparaToken ( "OP_VIRG", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( expressao ( ) );
			leioToken ( );
		}
		
		return eu;
	}

	private String expressao ( ArvorePrograma no ) throws Exception
	{			
		no = no.filho;
		
		//pego o tipo da primeira expressao
		String tipoPrimeiraExpressao = expressao_simples ( no );
		no = no.irmao; 
				
		//se não possui relacao
		if ( no == null )
			//retorno o tipo da primeira
			return tipoPrimeiraExpressao;
		
		//chamo a relacao
		String relacao = relacao ( no );
		
		//verifico tipo da relacao e tipo da primeira expressao
		if ( tipoPrimeiraExpressao.compareTo("bool") == 0 &&			
			 relacao.compareTo("OP_IGUAL") != 0 && relacao.compareTo("OP_DIFERE") != 0 )			
			throw new Exception ( "Relação invalida" );

		//vou para proxima expressao
		no = no.irmao;
		String tipoSegundaExpressao = expressao_simples ( no );
		
		//verifico se são do msm tipo
		if ( tipoPrimeiraExpressao.compareTo(tipoSegundaExpressao) != 0 )
			throw new Exception ( "Relação invalida: Incompatibilidade de tipos" );
		
		return tipoPrimeiraExpressao;
	}

	private void relacao ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "relacao" );

		if ( comparaToken ( "OP_IGUAL", 1 ) || comparaToken ( "OP_DIFERE", 1 ) || comparaToken ( "OP_MAIOR", 1 ) || 
			 comparaToken ( "OP_MENOR", 1 ) || comparaToken ( "OP_MENORIG", 1 ) || comparaToken ( "OP_MAIORIG", 1 ) )
			return eu;
		
		return null;
	}

	private String expressao_simples ( ArvorePrograma no ) throws Exception
	{
		String tipo, tipoAnterior = null;
		
		no = no.filho;		
		
		do
		{			
			//se tem operador
			if ( no.nomeNo.compareTo("operador") == 0)
			{
				//pego tipo operador
				if ( no.valor.compareTo("PA_OR") == 0)
					tipo = "bool";
				else
					tipo = "integer";
				
				//verifico o tipo
				if ( tipoAnterior != null && tipo.compareTo(tipoAnterior) != 0 )
					throw new Exception ("Incompatibilidade de tipos.");
			
				//passo o tipo para o anterior
				tipoAnterior = tipo;			
				
				//vou para o termo
				no = no.irmao;
			}
						
			//pego o tipo do termo
			tipo = termo ( no );
			
			
			//verifico o tipo
			if ( tipoAnterior != null && tipo.compareTo(tipoAnterior) != 0 )
				throw new Exception ("Incompatibilidade de tipos.");
			
			//passo o tipo para o anterior
			tipoAnterior = tipo;
			
			//caminha na arvore
			no = no.irmao;
		}
		while ( no != null );
		
		return tipo;
	}

	private String termo ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		String tipo, tipoAnterior = null;
		
		do
		{							
			//pego o tipo do fator
			tipo = fator ( no );
			
			//verifico o tipo
			if ( tipoAnterior != null && tipo.compareTo(tipoAnterior) != 0 )
				throw new Exception ("Incompatibilidade de tipos.");
			
			tipoAnterior = tipo;
			//caminha na arvore
			no = no.irmao;
			
			//se tem operador
			if ( no!= null)
			{
				//pego tipo operador
				if ( no.valor.compareTo("PA_AND") == 0)
					tipo = "bool";
				else
					tipo = "integer";
				
				//verifico o tipo
				if ( tipo.compareTo(tipoAnterior) != 0 )
					throw new Exception ("Incompatibilidade de tipos.");
				
				//vou para o termo
				no = no.irmao;
			}
		}
		while ( no != null );
		
	}

	private String fator ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		if ( no.nomeNo.compareTo( "fator_identificador" ) == 0 )
			return fator_identificador ( no );
	
		if ( no.nomeNo.compareTo( "expressao" ) == 0 )
			return expressao ( no );
		
		if ( no.nomeNo.compareTo( "numero" ) == 0 )
			return "integer";
		
		String tipo = fator ( no );
		
		if ( tipo.compareTo( "bool" ) != 0 )
			throw new Exception ( "Incompatibilidade de tipos." );
		
		return tipo;	
	}
	
	private String fator_identificador ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		
		//pego nome da chamada
		String identificador = no.valor; 
		
		no = no.irmao;
		
		//é uma varivel
		if ( no == null )			
			return declaracoes.pegaTipoVariavel ( identificador );
		
		//é uma funcao
		if ( no.nomeNo.compareTo("lst_expressoes") == 0 )			
			return validaFuncao ( identificador, no );
		
		//é um array ou um record
		return validaArrayRecord ( identificador, no );
	}

	private String validaArrayRecord(String identificador, ArvorePrograma no) {

		Tipo t = new TipoRecord ();
		
		
		return null;
	}

	private String validaFuncao(String identificador, ArvorePrograma no) throws Exception {
		
		int i = 0;
		
		TipoFuncao funcao = declaracoes.pegaFuncao ( identificador );
		
		if ( funcao == null )
			throw new Exception ("Chamada a função não declarada" );
		
		//vou para o primeiro parametro
		no = no.filho;

		while ( no != null )
		{
			//verifico tipo do parametro
			if ( funcao.pegaTipoParametro ( i++ ).compareTo ( expressao(no) ) != 0 )
				throw new Exception ("Tipo de parametro incompativel em chamada de função");
			
			no = no.irmao;
		}
		
		return funcao.pegaTipoRetorno ();
	}
}
