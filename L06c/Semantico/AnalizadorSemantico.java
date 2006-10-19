package Semantico;

import Sintatico.ArvorePrograma;

public class AnalizadorSemantico {
	
	private Declaracoes declaracoes;
	
	private int escopoAtual;
	
	private void caminhaArvore ( )
	{
		
		
	}
	
	public void validaArvore(ArvorePrograma arvorePrograma) throws Exception{		
	
		programa ( arvorePrograma );		
	}
		
	private void programa ( ArvorePrograma no ) throws Exception
	{
		no = no.filho;
		declaracoes.declara ( no.valor, no.pai, escopoAtual );

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
		escopoAtual++;
		
		no = no.filho;
		declaracoes ( no );	
			
		no = no.irmao;
		comando_composto ( no );
					
		escopoAtual--;
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
			if ( !declaracoes.declara(no.valor, no.pai, escopoAtual) )
				throw new Exception ( "Dupla declaracao do rotulo" + no.valor );
			
			no = no.irmao;
		}
		while ( no != null );
		
	}

	private void parte_const ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parte_const" );
		
		validaToken ( "PA_CONST", 1 );
		
		leioToken();
		eu.adicionaFilho( identificador() ); 
		
		while ( comparaToken ( "TO_ID", 0 ) )
		{
			//n�o chamo letoken antes pois dentro desta func vou usar o ID
			eu.adicionaFilho( decl_cte ( ) );	
		
			leioToken ( ); 
			validaToken ( "OP_PTVG", 1 );

			leioToken ( );
		}	 

		return eu;
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

	private void parte_tipos ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parte_tipos" );

		validaToken ( "PA_TYPEDEF", 1 );
		
		leioToken ( );
		eu.adicionaFilho( identificador() );
		
		while ( comparaToken ( "TO_ID", 0 ) )
		{
			//n�o chamo letoken antes pois dentro desta func vou usar o ID
			eu.adicionaFilho ( definicao_tipos ( ) );	
		
			leioToken ( );
			validaToken ( "OP_PTVG", 1 );
			
			leioToken ( );
		}	 

		return eu;
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

	private void parte_vars ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parte_vars" );
		
		validaToken ( "PA_VARS", 1 );
		
		leioToken ( );
		do 
		{		
			eu.adicionaFilho ( decl_vars ( ) );
			
			leioToken ( );
			validaToken ( "OP_PTVG", 1 );
			
			leioToken ( );

		} while ( comparaToken ( "TO_ID", 0 ) );
						
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

	private void parte_rotinas ( ) throws Exception
	{		
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parte_rotinas" );
		
		while ( comparaToken ( "PA_PROC", 1 ) || comparaToken ( "PA_FUNC", 1 ) )
		{
			if ( comparaToken ( "PA_PROC", 1 ) )
			{
				eu.adicionaFilho ( declaracao_procedimento ( ) );
				leioToken ( );
			}
			else 
			{
				eu.adicionaFilho ( declaracao_funcao ( ) );
				leioToken ( );
			}
		}

		return eu;
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
		
		expressao ( no );
		if ( no.tipo != "BOOLEANO" )
			throw new Exception ("PA"); 

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

	private void expressao ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "expressao" );
		
		eu.adicionaFilho ( expressao_simples ( ) );

		leioToken ( );
		if ( comparaToken ( "OP_IGUAL", 1 ) || comparaToken ( "OP_DIFERE", 1 ) || comparaToken ( "OP_MAIOR", 1 ) || 
			 comparaToken ( "OP_MENOR", 1 ) || comparaToken ( "OP_MENORIG", 1 ) || comparaToken ( "OP_MAIORIG", 1 ) )
		{
			eu.adicionaFilho ( relacao ( ) );

			leioToken ( );
			eu.adicionaFilho ( expressao_simples ( ) );
		}
		
		return eu;
	}

	private void relacao ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "relacao" );

		if ( comparaToken ( "OP_IGUAL", 1 ) || comparaToken ( "OP_DIFERE", 1 ) || comparaToken ( "OP_MAIOR", 1 ) || 
			 comparaToken ( "OP_MENOR", 1 ) || comparaToken ( "OP_MENORIG", 1 ) || comparaToken ( "OP_MAIORIG", 1 ) )
			return eu;
		
		return null;
	}

	private void expressao_simples ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "expressao_simples" );
		
		if ( comparaToken ( "OP_MAIS", 1 ) || comparaToken ( "OP_MENOS", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( termo ( ) );
		}
		else
			eu.adicionaFilho ( termo ( ) );
		
		leioToken ( );
		
		while ( comparaToken ( "OP_MAIS", 1 ) || comparaToken ( "OP_MENOS", 1 ) || comparaToken ( "PA_OR", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( termo ( ) );
			leioToken ( );
		}
		
		return eu;
	}

	private void termo ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "termo" );
		
		eu.adicionaFilho ( fator ( ) );

		leioToken ( );
		while ( comparaToken ( "OP_VEZES", 1 ) || comparaToken ( "OP_DIV", 1 ) || comparaToken ( "OP_RESTO", 1 ) || comparaToken ( "PA_AND", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( fator ( ) );
			leioToken ( );
		}
		
		return eu;
	}

	private void fator ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "fator" );
		
		if ( comparaToken ( "TO_ID", 0 ) )	
			eu.adicionaFilho ( fator_identificador ( ) );
			
		else if ( comparaToken ( "TO_IN", 0 ) )
			eu.adicionaFilho ( numero ( ) );

		else if ( comparaToken ( "OP_ABR_PAR", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( expressao ( ) );

			leioToken ( );
			validaToken ( "OP_FCH_PAR", 1 );
		}

		else if ( comparaToken ( "PA_NOT", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( fator ( ) );
		}
		else
			throw geraErro ( "TO_ID ou OP_ABR_PAR ou TO_IN ou PA_NOT", token[1] );
		
		return eu;
	}
	
	private void fator_identificador ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "fator_identificador" );

		eu.adicionaFilho( identificador() );
		
		leioToken ( );
		if ( comparaToken ( "OP_ABR_COLC", 1 ) || comparaToken ( "OP_PTO", 1 ) )
			eu.adicionaFilho ( variavel_parametros( ) );		
		
		else if ( comparaToken ( "OP_ABR_PAR", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( lst_expressoes( ) ); 			
			
			leioToken ( );
			validaToken ( "OP_FCH_PAR", 1 );
		}
		
		return eu;
	}
}