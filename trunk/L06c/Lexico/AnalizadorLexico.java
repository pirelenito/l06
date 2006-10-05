package Lexico;

import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Classe que pega os tokens do arquivo. 
 */
public class AnalizadorLexico 
{
	/**
	 * Descritor do arquivo
	 */
	private FileReader arquivo;
	
	/**
	 * Valor do ultimo caractere lido do arquivo
	 */
	private int caractere;
	
	/**
	 * Arvore RB contendo todos as palavras reservadas e seus respectivos tokens
	 */
	private TreeMap palavrasReservadas;
	
	/**
	 * Arvore RB contendo todos os operadores e seus respectivos tokens
	 */
	private TreeMap operadores;
	
	/**
	 * Valor de linha em que a leitura do arquivo está 
	 */
	private int linhaAtual;
	
	/**
	 * Constante que indica o erro para:
	 * 1 - Não achou o arquivo fonte 
	 */
	private static final short ERRO_NAO_ACHOU_ARQUIVO_FONTE = 1;
	
	/**
	 * Constante que indica o erro para:
	 * 2 - Comentário não fechado. 
	 */
	private static final short ERRO_COMENTARIO_NAO_FECHADO = 2;
	
	/**
	 * Constante que indica o erro para:
	 * 3 - Comentário não aberto. 
	 */
	private static final short ERRO_COMENTARIO_NAO_ABERTO = 3;
	
	/**
	 * Constante que indica o erro para:
	 * 4 - caractere inválido. 
	 */
	private static final short ERRO_CARACTERE_INVALIDO = 4;
	
	/**
	 * Constante que indica o erro para:
	 * 5 - identificador inválido 
	 */
	private static final short ERRO_IDENTIFICADOR_INVALIDO = 5;
	
	/**
	 * Constante que indica o erro para:
	 * 6 - número inválido 
	 */
	private static final short ERRO_NUMERO_INVALIDO = 6;
	
	/**
	 * Constante que indica o erro para:
	 * 7 - número muito grande
	 */
	private static final short ERRO_NUMERO_MUITO_GRANDE = 7;
		
	/**
	 * Valor constante para o maior numero suportado para um inteiro
	 */
	private static final int MAIOR_NUMERO = 65535;
	
	/**
	 * Construtor padrao do Analizador Lexico
	 * 
	 * @param caminhoArquivo contem o caminho para o arquivo de entrada
	 * @throws Exception no caso de ocorrer erro abrindo o arquivo!
	 */
	public AnalizadorLexico ( String caminhoArquivo ) throws Exception
	{
		//abro o arquivo
		try
		{
			arquivo = new FileReader ( caminhoArquivo );
		}
		catch ( Exception e )
		{ 
			throw new IOException ( "Erro abrindo o arquivo contendo o fonte" );
		}	
		
		//leio primeiro caractere
		caractere = leCaractere();
		
		//seto linhas = 1
		linhaAtual = 1;
		
		//inicializo arvores
		palavrasReservadas = new TreeMap ();
		operadores = new TreeMap ();
		
		//alimento a arvore contendo as palavras reservadas
		palavrasReservadas.put ( "PROG", "PA_PROG" );
		palavrasReservadas.put ( "TARGET", "PA_TARGET" );
		palavrasReservadas.put ( "CONST", "PA_CONST" );
		palavrasReservadas.put ( "IS", "PA_IS" );
		palavrasReservadas.put ( "TYPEDEF", "PA_TYPEDEF" );
		palavrasReservadas.put ( "ARRAY", "PA_ARRAY" );
		palavrasReservadas.put ( "RECORD", "PA_RECORD" );
		palavrasReservadas.put ( "END", "PA_END" );
		palavrasReservadas.put ( "VARS", "PA_VARS" );
		palavrasReservadas.put ( "PROC", "PA_PROC" );
		palavrasReservadas.put ( "FUNC", "PA_FUNC" );
		palavrasReservadas.put ( "BEGIN", "PA_BEGIN" );
		palavrasReservadas.put ( "GOTO", "PA_GOTO" );
		palavrasReservadas.put ( "IF", "PA_IF" );
		palavrasReservadas.put ( "THEN", "PA_THEN" );
		palavrasReservadas.put ( "ELSE", "PA_ELSE" );
		palavrasReservadas.put ( "FI", "PA_FI" );
		palavrasReservadas.put ( "FORALL", "PA_FORALL" );
		palavrasReservadas.put ( "IN", "PA_IN" );
		palavrasReservadas.put ( "DO", "PA_DO" );
		palavrasReservadas.put ( "WHILE", "PA_WHILE" );
		palavrasReservadas.put ( "OR", "PA_OR" );
		palavrasReservadas.put ( "AND", "PA_AND" );
		palavrasReservadas.put ( "NOT", "PA_NOT" );
		
		//alimento a arvore de operadores
		operadores.put ( "+", "OP_MAIS" );
		operadores.put ( "-", "OP_MENOS" );
		operadores.put ( "*", "OP_VEZES" );
		operadores.put ( "/", "OP_DIV" );
		operadores.put ( ".", "OP_PTO" );
		operadores.put ( ",", "OP_VIRG" );
		operadores.put ( ":", "OP_2PTO" );
		operadores.put ( ";", "OP_PTVG" );
		operadores.put ( "=", "OP_IGUAL" );
		operadores.put ( "<>", "OP_DIFERE" );
		operadores.put ( "<", "OP_MENOR" );
		operadores.put ( ">", "OP_MAIOR" );
		operadores.put ( "<=", "OP_MENORIG" );
		operadores.put ( ">=", "OP_MAIORIG" );
		operadores.put ( ":=", "OP_ATRIB" );
		operadores.put ( "..", "OP_PTOPTO" );
		operadores.put ( "!", "OP_EXCL" );
		operadores.put ( "(", "OP_ABR_PAR" );
		operadores.put ( ")", "OP_FCH_PAR" );
		operadores.put ( "%", "OP_RESTO" );
		operadores.put ( "[", "OP_ABR_COLC" );
		operadores.put ( "]", "OP_FCH_COLC" );
	}
	
	private boolean verificaLetra ( int a )
	{
		if ( ( a >= 'a' && a <= 'z' ) || ( a >= 'A' && a <= 'Z') )
			return true;
		
		return false;
	}
	
	private boolean verificaDigito ( int a )
	{
		if ( a >= '0' && a <= '9' )
			return true;
		
		return false;
	}
	
	/**
	 * Função helper que identifica se um caractere pode ser um operador
	 * 
	 * @param caractere lido do arquivo
	 * @return verdadeiro caso o caractere seja um operador
	 */
	private boolean verificaEspacador ( int caractere )
	{
		if ( caractere == -1 )
			return true;
		
		if ( caractere == ' ' )
			return true;
		
		if ( caractere == '\r' || caractere == '\n')
			return true;
		
		if ( caractere == '\t' )
			return true;
		
		if ( operadores.containsKey( String.valueOf( (char)caractere ) ) )
			return true;
		
		return false;
	}
	
	/**
	 * Função super util que quando cha,ada caminha pelo arquivo até chegar a um espaçador
	 * 
	 * @param palavra palavra sendo lida atualmente
	 * @return palavra passada por parametro mais todos os caracteres consumidos
	 */
	private String vaiAteProximoEspacador ( String palavra )
	{
		while ( !verificaEspacador ( caractere ) )
		{
			palavra = palavra + Character.toUpperCase( (char)caractere );
			
			caractere = leCaractere();	
		}
		
		return palavra;
	}
	
	/**
	 * Função que tem o simples proposito de ler um caractere do arquivo
	 * 
	 * @return caractere lido
	 */
	private int leCaractere ( )
	{
		try
		{
			//puxo do arquivo
			int c = arquivo.read();
			
			return c;
		}
		catch ( Exception e )
		{
			System.out.println ( e.toString() ); 
		}
		
		return -1;
	}

	/**
	 * Retorna a linha no qual o processo de analise sintatica se encontra atualmente
	 * 
	 * @return linha atual
	 */
	public int pegaLinhaAtual ( )
	{
		return linhaAtual;
	}	
	
	/**
	 * Retorna uma string contendo a descricao de um numero de erro
	 * 
	 * @param erro string contendo o numero do erro
	 * @return string formatada para impressão!
	 */
	public String pegaDescricaoErro ( String erro )
	{
		int numeroErro = Integer.decode ( erro ).intValue ( );
		
		String retorno = new String ( "" );
		
		switch ( numeroErro )
		{				
			case 1:
				retorno = "Nao achou o arquivo fonte";
			break;
			
			case 2:
				retorno = "Comentario nao fechado";
			break;
			
			case 3:
				retorno = "Comentario nao aberto";
			break;
			
			case 4:
				retorno = "Caractere invalido";
			break;
			
			case 5:
				retorno = "Identificador invalido";
			break;
			
			case 6:
				retorno = "Numero invalido";
			break;
			
			case 7:
				retorno = "Numero muito grande";
			break;
		}
		
		return retorno;
	}
	
	/**
	 * Função que consome e depois retorna o proximo token do arquivo
	 * 
	 * @return um array contendo na primeira posicao o tipo do token e em seguida o token!
	 */
	public String[] pegaToken ( )
	{	
		//seto estado inicial
		int estado = 1;
		
		//vetor de retorno
		String retorno[];
		retorno = new String[2];
		retorno[1] = new String ();				
							
		while ( true )
		{
			switch ( estado )
			{
				case 1:
					
					//inicio outra palavra
					retorno[1] = new String ();
					
					if ( caractere == '\n' )
						linhaAtual++;
					
					else if ( caractere == '\r' )
						break;
					
					else if ( verificaLetra( caractere ) )
						estado = 2;
					
					else if ( verificaDigito( caractere ) )
						estado = 3;
						
					else if ( caractere == '|' )
						estado = 4;
				
					else if ( caractere == '.' )
						estado = 9;
					
					else if ( caractere == '*' )
						estado = 7;
					
					else if ( caractere == '+' || caractere == '-' || 
						caractere =='/' || caractere ==',' || caractere =='%' || 
						caractere ==';' || caractere =='=' || caractere =='(' || 
						caractere ==')' || caractere =='[' || caractere ==']' || caractere =='!' )
						estado = 11;
					
					else if ( caractere == '>' ||  caractere == ':'  )
						estado = 12;
					
					else if ( caractere == '<' )
						estado = 10;
										
					else if ( caractere == ' ' || caractere == '\n' || caractere == '\r' || caractere == '\t' )
						estado = 1;
					
					else if ( caractere == -1 )
					{
						retorno[1] = " ";
						retorno[0] = "TO_EO";
						
						return retorno;
					}
					else
					{
						retorno[1] = Integer.toString( ERRO_CARACTERE_INVALIDO );
						retorno[0] = "TO_ER";
						
						//consumo proximo caractere
						caractere = leCaractere();
						
						return retorno;
					}
				break;
				
				case 2:
					if ( verificaLetra( caractere ) || verificaDigito( caractere ) )
						estado = 2;
					
					else if ( verificaEspacador( caractere ) )
					{							
						if ( palavrasReservadas.containsKey ( retorno[1] ) )
						{
							retorno[0] = "TO_PR";
							
							retorno[1] = (String)palavrasReservadas.get ( retorno[1] );						
						}
						else
						{
							retorno[0] = "TO_ID";
							
							retorno[1] = "'" + retorno[1] + "'";
						}
							
							
						return retorno;
					}
					else
					{
						vaiAteProximoEspacador ( retorno[1] );
						
						retorno[1] = Integer.toString( ERRO_IDENTIFICADOR_INVALIDO );
						retorno[0] = "TO_ER";
						
						return retorno;
					}
						
				break;
				
				case 3:
				
					if ( verificaDigito( caractere ) )
						estado = 3;
					
					else if ( verificaEspacador( caractere ) )
					{
						if ( Integer.decode ( retorno[1] ).intValue( ) > MAIOR_NUMERO )
						{
							retorno[1] = Integer.toString( ERRO_NUMERO_MUITO_GRANDE );
							retorno[0] = "TO_ER";
						}
						else
							retorno[0] = "TO_IN";
						
						return retorno;
					}
					else
					{
						vaiAteProximoEspacador ( retorno[1] );
						
						retorno[1] = Integer.toString( ERRO_NUMERO_INVALIDO );
						retorno[0] = "TO_ER";
						
						return retorno;
					}
					
				break;
				
				case 4:
					
					if ( caractere == '*' )
						estado = 5;
					
					else if ( caractere == '|' )
						estado = 8;
					
					else
					{
						retorno[1] = Integer.toString( ERRO_COMENTARIO_NAO_ABERTO );
						retorno[0] = "TO_ER";
						
						return retorno;
					}
					
				break;
				
				case 5:
					
					if ( caractere == '\n' )
						linhaAtual++;
					
					if ( caractere == '*' )
						estado = 6;
					
					else if ( caractere == -1 )
					{
						retorno[1] = Integer.toString( ERRO_COMENTARIO_NAO_FECHADO );
						retorno[0] = "TO_ER";
						
						return retorno;
					}
					else 
						estado = 5;					
					
				break;
				
				case 6:
					
					if ( caractere == '*' )
						estado = 6;
					
					else if ( caractere == '|' )
						estado = 1;
					
					else if ( caractere == -1 )
					{
						retorno[1] = Integer.toString( ERRO_COMENTARIO_NAO_FECHADO );
						retorno[0] = "TO_ER";
						
						return retorno;
					}
					
					else 
						estado = 5;					
					
				break;				

				case 7:
					
					if( caractere == '|')
					{
						retorno[1] = Integer.toString( ERRO_COMENTARIO_NAO_ABERTO );
						retorno[0] = "TO_ER";
						
						//consumo proximo caractere
						caractere = leCaractere();
						
						return retorno;						
					}	
					else
					{
						retorno[0] = "TO_OP";
						
						retorno[1] = (String)operadores.get ( retorno[1] );
															
						return retorno;						
					}	
				
				case 8:
					
					if ( caractere == -1 )
						estado = 1;
					
					else if ( caractere != '\n' )
						estado = 8;
					
					else 
					{
						linhaAtual++;
						estado = 1;					
					}
					
				break;
				
				case 9:
					
					if ( caractere == '.' )
						estado = 11;
					
					else 
					{
						retorno[0] = "TO_OP";
						
						retorno[1] = (String)operadores.get ( retorno[1] );
															
						return retorno;						
					}
						
				break;
				
				case 10:
					
					if ( caractere == '=' || caractere == '>' )
						estado = 11;
					
					else 
					{
						retorno[0] = "TO_OP";
						
						retorno[1] = (String)operadores.get ( retorno[1] );
															
						return retorno;						
					}
						
				break;
				
				case 11:					
					
					retorno[0] = "TO_OP";
					
					retorno[1] = (String)operadores.get ( retorno[1] );
														
					return retorno;			
				
				case 12:
					
					if ( caractere == '=' )
						estado = 11;
					
					else
					{
						retorno[0] = "TO_OP";
						
						retorno[1] = (String)operadores.get ( retorno[1] );
															
						return retorno;						
					}
						
				break;
				
				default:
					
					System.out.println ( "Erro na maquina de estado! Foi para onde não devia!" );
						
				break;
			}
			
			//concateno na resposta
			retorno[1] = retorno[1] + Character.toUpperCase( (char)caractere );
			
			//consumo proximo caractere
			caractere = leCaractere();
		}
		
	}
	
}
