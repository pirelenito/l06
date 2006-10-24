package Sintatico;

import Lexico.AnalizadorLexico;

/**
 * Classe que faz a analise sintatica. 
 */
public class AnalizadorSintatico 
{
	/**
	 * Variavel que armazena o token atual
	 */
	private String token[];
	
	/**
	 * Flag que indica se um toke foi consumido
	 */
	private boolean tokenConsumido;
	
	/**
	 * Numero do proximo nó a ser gerado
	 */
	private int numeroNo;
	
	/**
	 * Ponteiro para a instancia do analizador lexico
	 */
	private AnalizadorLexico analizadorLexico;
	
	/**
	 * Construtor padrão
	 * 
	 * @param _analizadorLexico recebe o ponteiro para um analizador no inicio do arquivo
	 */
	public AnalizadorSintatico ( AnalizadorLexico _analizadorLexico )
	{
		analizadorLexico = _analizadorLexico;
		
		numeroNo = 0;
		
		tokenConsumido = true;
	}	
	
	/**
	 * Função que le o token se o mesmo já foi consumido
	 */
	private void leioToken ( ) throws Exception
	{
		if ( tokenConsumido )
		{
			token = analizadorLexico.pegaToken ( );
			
			if ( token[0].compareTo( "TO_ER" ) == 0 )
				throw new Exception ( "Linha " + analizadorLexico.pegaLinhaAtual() + ": " + analizadorLexico.pegaDescricaoErro ( token[1] ) );	
			
			tokenConsumido = false;
		}
	}
	
	/**
	 * Função que compara o valor passado com o token atual
	 * Tambem atualiza como consumido se der verdadeiro!
	 * 
	 * @param valor a ser comaparado
	 * @param posicao (0 ou 1) posicoes do vetor do token
	 * @return sucesso se esta igual
	 */
	private boolean comparaToken ( String valor, int posicao  )
	{
		if ( valor.compareTo ( token[posicao] ) == 0 )
			return tokenConsumido = true;
		
		return false; 
	}
	
	/**
	 * Verifica a igualdade de um token, lancando uma exceção quando diferente do esperado!
	 * Tambem atualiza como consumido se der verdadeiro!
	 * 
	 * @param valor a ser comaparado
	 * @param posicao (0 ou 1) posicoes do vetor do token
	 * @return sucesso se esta igual
	 * @throws Exception solta uma exceção caso esteja diferente do esperado
	 */
	private boolean validaToken ( String valor, int posicao ) throws Exception
	{
		tokenConsumido = true;
		
		if ( !comparaToken ( valor, posicao ) )
			throw geraErro ( valor, token[posicao] );
		
		return true;
	}
	
	/**
	 * Monta a string do erro conforme a formatação interna
	 * 
	 * @param esperado token esperado
	 * @param encontrado token encontrado
	 * @return retorna uma excessao pronta para lançar!
	 */
	private Exception geraErro ( String esperado, String encontrado )
	{
		return new Exception ( "Linha " + analizadorLexico.pegaLinhaAtual() + ": Esperado: " + esperado + " Encontrado: " + encontrado );
	}
	
	/**
	 * Retorna a arvore de programa
	 * 
	 * @return ponteiro para a raiz da arvore
	 * @throws Exception caso haja algum erro na geração da arvore
	 */
	public ArvorePrograma pegaArvorePrograma ( ) throws Exception
	{
		leioToken ( );
		
		return programa ( );		
	}
	
	private ArvorePrograma programa ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "programa" );

		validaToken ( "PA_PROG", 1 );
		
		leioToken ( );
		eu.adicionaFilho( identificador ( ) );

		leioToken ( );
		eu.adicionaFilho ( bloco ( ) );

		leioToken ( );
		validaToken ( "OP_PTO" , 1 );
		
		return eu;
	}
	
	private ArvorePrograma identificador ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "identificador" );
		
		validaToken ( "TO_ID", 0 );

		eu.valor = token[1];
		
		return eu;
	}

	private ArvorePrograma numero ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "numero" );
		
		validaToken ( "TO_IN" , 0 );

		eu.valor = token[1];
		
		return eu;
	}
	

	private ArvorePrograma operador ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "operador" );
		
		if ( !( comparaToken ( "OP_MAIS", 1 ) || comparaToken ( "OP_MENOS", 1 ) || 
				comparaToken ( "PA_OR", 1 ) || comparaToken ( "OP_VEZES", 1 ) || 
				comparaToken ( "OP_DIV", 1 ) || comparaToken ( "OP_RESTO", 1 ) || 
				comparaToken ( "PA_AND", 1 ) || comparaToken ( "OP_EXCL", 1 ) ) )			
			
			geraErro( "operador", eu.valor );
			
		eu.valor = token[1];
		
		return eu;
	}

	private ArvorePrograma bloco ( ) throws Exception
	{	
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "bloco" );
		/*		
		while ( comparaToken ( "PA_TARGET", 1 ) || comparaToken ( "PA_CONST", 1 ) || comparaToken ( "PA_TYPEDEF", 1 ) || 
			    comparaToken ( "PA_VARS", 1 ) || comparaToken ( "PA_PROC", 1 ) || comparaToken ( "PA_FUNC", 1 ) )
		{*/
		
		eu.adicionaFilho ( declaracoes ( ) );
		leioToken ( );		
			
		leioToken ( );
		eu.adicionaFilho ( comando_composto ( ) );
					
		return eu;
	}

	private ArvorePrograma declaracoes ( ) throws Exception
	{
		//declaracoes ::=
		//[parte_rotulos] [parte_const] [parte_tipos] [parte_vars] [parte_rotinas]
		
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "declaracoes" );
		
		if ( comparaToken ( "PA_TARGET", 1 ) )
		{
			eu.adicionaFilho ( parte_rotulos ( ) );	
			leioToken ( );
		}
		
		if ( comparaToken ( "PA_CONST", 1 ) )
		{
			eu.adicionaFilho ( parte_const ( ) );		
			leioToken ( );
		}

		if ( comparaToken ( "PA_TYPEDEF", 1 ) )
		{
			eu.adicionaFilho ( parte_tipos ( ) );
			leioToken ( );
		}

		if ( comparaToken ( "PA_VARS", 1 ) )
		{
			eu.adicionaFilho ( parte_vars ( ) );
			leioToken ( );
		}

		if ( comparaToken ( "PA_PROC", 1 ) || comparaToken ( "PA_FUNC", 1 ) )
		{
			eu.adicionaFilho ( parte_rotinas ( ) );
			leioToken ( );
		}
		
		return eu;
	}

	private ArvorePrograma parte_rotulos ( ) throws Exception
	{	
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parte_rotulos" );
		
		validaToken ( "PA_TARGET", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( numero ( ) );
		
		leioToken ( );
		while ( comparaToken ( "OP_VIRG", 1 ) )
		{
			leioToken ( ); 
			eu.adicionaFilho ( numero ( ) );
			leioToken ( ); 
		} 
		
		validaToken ( "OP_PTVG", 1 );

		return eu;
	}

	private ArvorePrograma parte_const ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parte_const" );
		
		validaToken ( "PA_CONST", 1 );
		
		leioToken();
		eu.adicionaFilho( identificador() ); 
		
		while ( comparaToken ( "TO_ID", 0 ) )
		{
			//não chamo letoken antes pois dentro desta func vou usar o ID
			eu.adicionaFilho( decl_cte ( ) );	
		
			leioToken ( ); 
			validaToken ( "OP_PTVG", 1 );

			leioToken ( );
		}	 

		return eu;
	}

	private ArvorePrograma decl_cte ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "decl_cte" );
		
		validaToken( "TO_ID", 0 );
		
		leioToken ( );
		validaToken ( "PA_IS", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( expressao ( ) );

		return eu;
	}

	private ArvorePrograma parte_tipos ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "parte_tipos" );

		validaToken ( "PA_TYPEDEF", 1 );
		
		leioToken ( );		
		
		while ( comparaToken ( "TO_ID", 0 ) )
		{
			//não chamo letoken antes pois dentro desta func vou usar o ID
			eu.adicionaFilho ( definicao_tipos ( ) );	
		
			leioToken ( );
			validaToken ( "OP_PTVG", 1 );
			
			leioToken ( );
		}	 

		return eu;
	}

	private ArvorePrograma definicao_tipos ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "definicao_tipos" );
		
		eu.adicionaFilho( identificador() );
				
		leioToken ( );
		validaToken ( "OP_IGUAL", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( tipo ( ) );

		return eu;
	}

	private ArvorePrograma tipo ( ) throws Exception
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

	private ArvorePrograma tipo_array ( ) throws Exception
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

	private ArvorePrograma indice ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "indice" );
		
		eu.adicionaFilho ( numero ( ) );	

		leioToken ( );
		validaToken ( "OP_PTOPTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( numero ( ) );	

		return eu;
	}

	private ArvorePrograma tipo_registro ( ) throws Exception
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


	private ArvorePrograma lst_campo ( ) throws Exception
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

	private ArvorePrograma campo ( ) throws Exception
	{	
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "campo" );
		
		eu.adicionaFilho ( lst_identificadores ( ) );
		
		leioToken ( );
		validaToken ( "OP_2PTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( tipo ( ) );
		
		return eu;
	}

	private ArvorePrograma lst_identificadores ( ) throws Exception
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

	private ArvorePrograma parte_vars ( ) throws Exception
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

	private ArvorePrograma decl_vars ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "decl_vars" );
		
		eu.adicionaFilho ( lst_identificadores ( ) );
		
		leioToken ( );
		validaToken ( "OP_2PTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( tipo ( ) );	
		
		return eu;
	}

	private ArvorePrograma parte_rotinas ( ) throws Exception
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

	private ArvorePrograma declaracao_procedimento ( ) throws Exception
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

	private ArvorePrograma declaracao_funcao ( ) throws Exception
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

	private ArvorePrograma parametros_formais ( ) throws Exception
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

	private ArvorePrograma par_formal ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "par_formal" );
		
		eu.adicionaFilho ( lst_identificadores_par ( ) );

		leioToken ( );
		validaToken ( "OP_2PTO", 1 );
		
		leioToken ( );
		eu.adicionaFilho ( identificador ( ) );
		
		return eu;
	}

	private ArvorePrograma lst_identificadores_par ( ) throws Exception
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

	private ArvorePrograma par ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "par" );
		
		if ( comparaToken ( "OP_EXCL", 1 ) )
		{
			eu.adicionaFilho( operador ( ) );
			leioToken ( );
		}

		eu.adicionaFilho ( identificador ( ) );
		
		return eu;
	}

	private ArvorePrograma comando_composto ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando_composto" );
		
		validaToken ( "PA_BEGIN", 1 );
			
		do
		{	
			leioToken ( );
			if ( comparaToken ( "PA_END", 1 ) )
			{
				validaToken ( "PA_END", 1 );
				return eu;
			}
			
			eu.adicionaFilho ( comando ( ) );
					
			leioToken ( );
				
		}while ( validaToken ( "OP_PTVG", 1 ) );
				
		return eu;
	}

	private ArvorePrograma comando ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando" );
		
		if ( comparaToken ( "TO_IN", 0 ) )
		{
			eu.adicionaFilho ( numero ( ) );
			
			leioToken ( );
			validaToken ( "OP_2PTO", 1 );
			
			leioToken ( );
		}

		eu.adicionaFilho ( comando_sem_rotulo ( ) );
		
		return eu;
	}

	private ArvorePrograma comando_sem_rotulo ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando_sem_rotulo" );
		
		if ( comparaToken ( "TO_ID", 0 ) )
			eu.adicionaFilho ( comando_sem_rotulo_identificador ( ) );
					
		else if ( comparaToken ( "PA_GOTO", 1 ) )
			eu.adicionaFilho ( desvio ( ) );

		else if ( comparaToken ( "PA_BEGIN", 1 ) )
			eu.adicionaFilho ( comando_composto ( ) );

		else if ( comparaToken ( "PA_IF", 1 ) )
			eu.adicionaFilho ( comando_condicional ( ) );

		else if ( comparaToken ( "PA_FORALL", 1 ) )
			eu.adicionaFilho ( comando_for ( ) );

		else if ( comparaToken ( "PA_WHILE", 1 ) )
			eu.adicionaFilho ( comando_while ( ) );
		else
			throw geraErro ( "identificador ou GOTO ou BEGIN ou IF ou FORALL ou WHILE", token[1] );
		
		return eu;
	}

	private ArvorePrograma  comando_sem_rotulo_identificador ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando_sem_rotulo_identificador" );
		
		eu.adicionaFilho( identificador() );
		
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
	
	private ArvorePrograma variavel ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "variavel" );
		
		identificador();
		
		leioToken ( );
		if ( comparaToken ( "OP_ABR_COLC", 1 ) || comparaToken ( "OP_PTO", 1 ) )
			eu.adicionaFilho ( variavel_parametros ( ) );
		
		return eu;
	}

	private ArvorePrograma variavel_parametros ( ) throws Exception
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
	
	private ArvorePrograma desvio ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "desvio" );
		
		validaToken ( "PA_GOTO", 1 );

		leioToken ( );
		eu.adicionaFilho ( numero ( ) );
		
		return eu;
	}

	private ArvorePrograma comando_condicional ( ) throws Exception
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

	private ArvorePrograma comando_for ( ) throws Exception
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

	private ArvorePrograma comando_while ( ) throws Exception 
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "comando_while" );
		
		validaToken ( "PA_WHILE", 1 );

		leioToken ( );
		eu.adicionaFilho ( expressao ( ) );

		leioToken ( );
		validaToken ( "PA_DO", 1 );

		leioToken ( );
		eu.adicionaFilho ( comando_sem_rotulo ( ) );
		
		return eu;
	}

	private ArvorePrograma lst_expressoes ( ) throws Exception
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

	private ArvorePrograma expressao ( ) throws Exception
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

	private ArvorePrograma relacao ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "relacao" );

		if ( comparaToken ( "OP_IGUAL", 1 ) || comparaToken ( "OP_DIFERE", 1 ) || comparaToken ( "OP_MAIOR", 1 ) || 
			 comparaToken ( "OP_MENOR", 1 ) || comparaToken ( "OP_MENORIG", 1 ) || comparaToken ( "OP_MAIORIG", 1 ) )
			return eu;
		
		return null;
	}

	private ArvorePrograma expressao_simples ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "expressao_simples" );
		
		if ( comparaToken ( "OP_MAIS", 1 ) || comparaToken ( "OP_MENOS", 1 ) )
		{
			eu.adicionaFilho ( operador ( ) );
			leioToken ( );
			eu.adicionaFilho ( termo ( ) );
		}
		else
			eu.adicionaFilho ( termo ( ) );
		
		leioToken ( );
		
		while ( comparaToken ( "OP_MAIS", 1 ) || comparaToken ( "OP_MENOS", 1 ) || comparaToken ( "PA_OR", 1 ) )
		{
			eu.adicionaFilho ( operador ( ) );
			leioToken ( );
			eu.adicionaFilho ( termo ( ) );
			leioToken ( );
		}
		
		return eu;
	}

	private ArvorePrograma termo ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "termo" );
		
		eu.adicionaFilho ( fator ( ) );

		leioToken ( );
		while ( comparaToken ( "OP_VEZES", 1 ) || comparaToken ( "OP_DIV", 1 ) || comparaToken ( "OP_RESTO", 1 ) || comparaToken ( "PA_AND", 1 ) )
		{
			eu.adicionaFilho ( operador ( ) );
			leioToken ( );
			eu.adicionaFilho ( fator ( ) );
			leioToken ( );
		}
		
		return eu;
	}
	
	private ArvorePrograma fator ( ) throws Exception
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
	
	private ArvorePrograma fator_identificador ( ) throws Exception
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

	private ArvorePrograma chamada_funcao ( ) throws Exception
	{
		ArvorePrograma eu = new ArvorePrograma ( numeroNo++, "fator" );
		
		validaToken ( "TO_ID", 0 );
		
		leioToken ( );
		if ( comparaToken ( "OP_ABR_PAR", 1 ) )
		{
			leioToken ( );
			eu.adicionaFilho ( lst_expressoes ( ) );

			leioToken ( );
			validaToken ( "OP_FCH_PAR", 1 );
		}
		
		return eu;
	}
}