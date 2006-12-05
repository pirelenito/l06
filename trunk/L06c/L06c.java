
import java.io.*;
import java.util.ArrayList;

import Codigo.GeradorDeCodigo;
import Lexico.AnalizadorLexico;
import Semantico.AnalizadorSemantico;
import Sintatico.*;

/**
 * Classe principal do compilador, recebe os parametros e aponta execuções
 * 
 * @author Paulo Vitor Ragonha
 * @author Tiago Kohagura
 */
public class L06c 
{
	/**
	 * Constante utilizada no sistema para pegar os parametros da linha de comando
	 * 
	 * aqui indica que nao foi escolhido um modo
	 */
	private final static short MODO_NAO_EXPECIFICADO = 0;
	
	/**
	 * Constante utilizada no sistema para pegar os parametros da linha de comando
	 * 
	 * -L arq: executa apenas o analisador léxico, emitindo mensagens de erro na saida padrao e gravando o relatorio no arquivo arq.  [LEX]
	 */
	private final static short MODO_LEXICO = 1;
	
	/**
	 * Constante utilizada no sistema para pegar os parametros da linha de comando
	 * 
	 * -SI arq: executa até o analisador sintatico, emitindo mensagens de erro na saida padrao e gravando o relatorio no arquivo arq. [SINT]
	 */
	private final static short MODO_SINTATICO = 2;
	
	/**
	 * Constante utilizada no sistema para pegar os parametros da linha de comando
	 * 
	 * -SE: executa até a analise semantica e coloca erros reportados (léxicos, sintaticos, semanticos) no arquivo arq, segundo o padrao usual para mensagens de erro. [SEMAN]
	 */
	private final static short MODO_SEMANTICO = 3;
	
	/**
	 * Constante utilizada no sistema para pegar os parametros da linha de comando
	 * 
	 * -R arq: executa todas as fases do compilador, emitindo mensagens de erro na saida padrao e grava os relatorios de cada uma das fases no arquivo arq.
	 */
	private final static short MODO_COMPLETO = 4;
	
	/**
	 * Constante utilizada no sistema para pegar os parametros da linha de comando
	 * 
	 * -I: imprime na saida padrao a identificacao do compilador e da equipe. Nao executa nenhuma fase do compilador. Exemplo:
	 */
	private final static short MODO_SOBRE = 5;
	
	/**
	 * Constante utilizada no sistema para pegar os parametros da linha de comando
	 * 
	 * -?: imprime na saida padrao um resumo das opcoes do compilador. Nao executa nenhuma fase do compilador.
	 */
	private final static short MODO_AJUDA = 6;	
	
	private static boolean percursoInterativoAP;
	
	/**
	 * String contendo o nome do arquivo onde sera escrito o relatorio
	 */
	private static String arquivoRelatorio;
	
	/**
	 * String contendo o nome do arquivo contendo o fonte
	 */
	private static String arquivoEntrada;
	
	/**
	 * String contendo o caminho para gravar as saidas
	 */
	private static String diretorioSaida;
	
	/**
	 * String contendo o caminho para ler a entrada
	 */
	private static String diretorioEntrada;	
	
	/**
	 * Variavel contendo o modo de funcionamento inicial
	 */
	private static short modo = MODO_NAO_EXPECIFICADO;
	
	/**
	 * Descritores para o arquivo de saida do relatorio
	 * 
	 * Posições:
	 * 0 - Léxico
	 * 1 - Sintatico
	 * 2 - Semantico
	 * 3 - Geracao de código
	 */
	private static FileWriter[] relatorioSaida;
	
	/**
	 * Analisador Lexico do compilador
	 */
	private static AnalizadorLexico analizadorLexico;
	
	/**
	 * Analisador Sintatico do compildar
	 */
	private static AnalizadorSintatico analizadorSintatico;
	
	/**
	 * Analisador Semantico do compilador
	 */
	private static AnalizadorSemantico analizadorSemantico;
	
	private static GeradorDeCodigo geradorDeCodigo;
	
	/**
	 * Funcao helper para verifiar se uma string contem a extencao .l06
	 * 
	 * @param arquivo string contendo o valor a ser analizado
	 * @return verdadeiro se contem
	 */
	private static boolean verificaEstencao ( String arquivo )
	{
		if ( arquivo.substring( arquivo.length() - 4 ).toUpperCase().compareTo( ".L06" ) == 0 )
			return true;
		
		return false;
	}
	
	/**
	 * Funcao que le os argumentos e identifica o modo de funcionameto e os caminhos 
	 * 
	 * @param args argumentos vindos do SO
	 * @throws Exception no caso de erro!
	 */
	private static void leParametros ( String[] args ) throws Exception
	{	
		//constantes para controle
		final short ESPERANDO_NADA = 0;
		final short ESPERANDO_DIR_SAIDA = 1;
		final short ESPERANDO_DIR_ENTRADA = 2;
		final short ESPERANDO_ARQ_RELATORIO = 3;
		
		short esperando = ESPERANDO_NADA;
		percursoInterativoAP = false;
		
		if ( args.length == 0 )
		{
			modo = MODO_AJUDA;
			return;
		}
		
		for ( int i = 0; i < args.length; i++ )
		{
			if ( esperando == ESPERANDO_NADA )
			{			
				//primeiro estado
				if ( args[i].compareTo( "-L" ) == 0 )
				{
					if ( modo == MODO_NAO_EXPECIFICADO )
					{
						modo = MODO_LEXICO;
						esperando = ESPERANDO_ARQ_RELATORIO;
					}
					else 
						throw new Exception ( "Mais de um modo expecificado!" );
				}
				else if ( args[i].compareTo( "-SI" ) == 0 )
				{
					if ( modo == MODO_NAO_EXPECIFICADO )
					{
						modo = MODO_SINTATICO;
						esperando = ESPERANDO_ARQ_RELATORIO;
					}
					else 
						throw new Exception ( "Mais de um modo expecificado!" );
				}
				else if ( args[i].compareTo( "-SE" ) == 0 )
				{
					if ( modo == MODO_NAO_EXPECIFICADO )
					{
						modo = MODO_SEMANTICO;
						esperando = ESPERANDO_ARQ_RELATORIO;
					}
					else 
						throw new Exception ( "Mais de um modo expecificado!" );
				}
				else if ( args[i].compareTo( "-R" ) == 0 )
				{
					if ( modo == MODO_NAO_EXPECIFICADO )
					{
						modo = MODO_COMPLETO;
						esperando = ESPERANDO_ARQ_RELATORIO;
					}
					else 
						throw new Exception ( "Mais de um modo expecificado!" );		
				}
				else if ( args[i].compareTo( "-I" ) == 0 )
				{
					if ( modo == MODO_NAO_EXPECIFICADO )
					{	
						modo = MODO_SOBRE;
					}
					else 
						throw new Exception ( "Mais de um modo expecificado!" );
				}
				else if ( args[i].compareTo( "-?" ) == 0 )
				{
					if ( modo == MODO_NAO_EXPECIFICADO )
					{	
						modo = MODO_AJUDA;
					}
					else 
						throw new Exception ( "Mais de um modo expecificado!" );			
				}				
				else if ( args[i].compareTo( "-DS" ) == 0 )
				{
					esperando = ESPERANDO_DIR_SAIDA;
				}
				else if ( args[i].compareTo( "-DF" ) == 0 )
				{
					esperando = ESPERANDO_DIR_ENTRADA;
				}				
				else if ( args[i].compareTo( "-GL" ) == 0 )
				{
					//-GL: produz arquivo arquivo.mes  (codigo MEPA, com rotulos em lugar de enderecos -- ver Geracao de Codigo ).  [GER]
				}
				else if ( args[i].compareTo( "-GC" ) == 0 )
				{
					//-GC:  nao coloca comentarios no codigo gerado (.mep ou .mes)  [GER]
				}
				else if ( args[i].compareTo( "-BSI" ) == 0 )
				{
					//-BSI: percurso iterativo na arvore de programa. Executado apos terminar a construcao da arvore de programa e antes de iniciar a geracao de codigo. Apos executar percurso iterativo, a compilacao continua normalmente. (Esta opcao sera util para depuracao). Ver Saida do Analisador Sintatico . [SINT]
					percursoInterativoAP = true;
				}
				else
				{	
					if ( verificaEstencao ( args[i] ) )
						arquivoEntrada = args[i];
					else
						throw new Exception ( "Parametro ou arquivo invalido!" );
				}
								
			}
							
			else 
			{
						
				if ( esperando == ESPERANDO_ARQ_RELATORIO )
				
					arquivoRelatorio = args[i];
				
				else if ( esperando == ESPERANDO_DIR_ENTRADA )
					diretorioEntrada = args[i];
				
				else if ( esperando == ESPERANDO_DIR_SAIDA )
					diretorioSaida = args[i];
			
				esperando = ESPERANDO_NADA;			
			}
		}
		
		if ( modo == MODO_NAO_EXPECIFICADO )
			modo = MODO_COMPLETO;
					
		return;
	}
	
	/**
	 * Funcao main
	 * 
	 * @param args
	 */
	public static void main( String[] args ) 
	{		
		//leio os parametros
		try 
		{
			leParametros ( args );
		}
		catch ( Exception e )
		{
			modoAjuda ( );
			
			return;
		}
		
		//arrumo caminho de saida
		if ( diretorioSaida != null )
		{
			if ( diretorioSaida.substring( diretorioSaida.length() - 1 ).compareTo( "\\" ) != 0 )
				diretorioSaida = diretorioSaida + "\\";
		}
		else
		{
			diretorioSaida = "";
		}
		
		//arrumo caminho de entrada
		if ( diretorioEntrada != null )
		{
			if ( diretorioEntrada.substring( diretorioEntrada.length() - 1 ).compareTo( "\\" ) != 0 )
				diretorioEntrada = diretorioEntrada + "\\";
		}
		else
		{
			diretorioEntrada = "";
		}
		
		try 
		{
			//executo o modo desejado
			switch ( modo )
			{
				case ( MODO_AJUDA ):
					modoAjuda ( );
				break;
				
				case ( MODO_SOBRE ):
					modoSobre ( );
				break;
				
				case ( MODO_COMPLETO ):
					modoCompleto ( );
				break;
				
				case ( MODO_LEXICO ):
					modoLexico ( );
				break;
				
				case ( MODO_SEMANTICO ):
					modoSemantico ( );
				break;
				
				case ( MODO_SINTATICO ):
					modoSintatico ( );
				break;
				
			}
			
		}
		catch ( Exception e )
		{
			System.out.println( "!!Erro encontrado: \n" );
			System.err.println( "\t" + e.getMessage() );
			System.out.println( " " );
		
			fechaArquivosRelatorio ( );
			return;
		}
		
		fechaArquivosRelatorio ( );
		System.out.println( "OK." );
	}

	/**
	 * Retorna a extenção para o arquivo de relatorio
	 * 
	 * @param passo (lexico, sintanti, semantico, geração de codigo)
	 * @return String contendo a estencao
	 */
	private static String pegaEstencao ( int passo )
	{
		switch ( passo )
		{
			case 0:
				return new String ( ".lex" );
				
			case 1:
				return new String ( ".sin" );
				
			case 2:
				return new String ( ".sem" );
				
			case 3:
				return new String ( ".cod" );
		}
		
		return new String ( "" );
	}
	
	/**
	 * Escreve na saida do relatorio caso o arquivo esteja aberto
	 * Ja trata erros internamente
	 * 
	 * @param argumento string a ser escrita
	 */
	private static void escreveNoRelatorio ( int passo, String argumento ) throws Exception
	{
		//se não tem relatorio
		if ( arquivoRelatorio == null )
			return;
		
		//instancia vetor (1° vez)
		if ( relatorioSaida == null )
			relatorioSaida = new FileWriter[4];
					
		//instancia relatorio expecifico
		if ( relatorioSaida[passo] == null )
		{
			try 
			{
				//abre o arquivo expecifico
				relatorioSaida[passo] = new FileWriter ( diretorioSaida + arquivoRelatorio + pegaEstencao ( passo ) );
			}
			catch ( Exception e )
			{
				throw new Exception ( "Impossivel abrir arquivo saida do relatorio: " + diretorioSaida + arquivoRelatorio + pegaEstencao ( passo ) );
			}	
		}
		
		try 
		{
			relatorioSaida[passo].write ( argumento );
		}
		catch ( Exception io )
		{
			throw new Exception ( "Impossivel gravar relatorio!" + diretorioSaida + arquivoRelatorio + pegaEstencao ( passo ) );
		}			
	}
	
	/**
	 * Verifica se foi aberto algum arquivo de relatorio e fecha os mesmos
	 */
	private static void fechaArquivosRelatorio ( ) 
	{
		//se nao tem relatorio de saida jah saio
		if ( relatorioSaida == null )
			return;
		
		//do contrario fecho arquivo para gravar relatorio em disco
		try 
		{
			for ( int i = 0; i < 4; i++ )
				if ( relatorioSaida[i] != null )
					relatorioSaida[i].close();
		}
		catch ( Exception io )
		{
			System.err.println ( "Erro: Fechando arquivos de relatorio!" );
		}
		
	}
	
	/**
	 * Modo de funciomamento do compilador onde e exibido que fez o mesmo!
	 */
	private static void modoSobre ( )
	{
		System.out.println ( "\nCompilador L06    v0.8       2006 \n" +
							 "Paulo Vitor       matricula: 200405600517\n" +
							 "Tiago Kohagura    matricula: 200405600528\n" ); 
	}
	
	/**
	 * Modo de funcionamento onde e exibido a ajuda para o usuario
	 */
	private static void modoAjuda ( )
	{
		System.out.println ( "\nUtilizacao:  L06c [-DS dir] [-DF dir] [-L arq] [-I]  [-?] \n" +
							 "             [-SI arq] [-SE arq] [-GL] [-GC] [-BSI] [-R arq] [arquivo.l06]\n\n" + 
							 "-DF dir: diretorio onde estao os arquivos-fonte .\n" +
							 "-DS dir: diretorio onde serao gravados todos os arquivos produzidos.\n" +
							 "-L arq: apenas o analisador lexico, gravando o relatorio no arquivo arq.\n" +
							 "-R arq: todas as fases do compilador, gravando os relatorios no arquivo arq.\n" + 
							 "-SI arq: ate o analisador sintatico, gravando os relatorios no arquivo arq.\n" +
							 "-BSI: percurso iterativo na arvore de programa. \n" +
							 "-SE: executa ate a analise semantica e coloca erros reportados. \n" +
							 "-GL: produz arquivo arquivo.mes. \n" +
							 "-GC:  nao coloca comentarios no codigo gerado (.mep ou .mes).\n" +
							 "-I: Versão e desenvolvedores do compilador.\n" + 
							 "-?: Ajuda.\n" );

	}
	
	/**
	 * Carrega o analizador lexico passando os parametros de arquivo
	 */
	private static void carregaLexico ( ) throws Exception
	{
		System.out.println ( "[" + arquivoEntrada + "]>>Carregando Lexico..." );
		
		//inicio o modo lexico
		try
		{
			analizadorLexico = new AnalizadorLexico ( diretorioEntrada +  arquivoEntrada );
		}
		catch ( Exception e )
		{
			throw new Exception ( "Linha 0: [TO_ER,1] => Nao achou o arquivo fonte" );
		}
	}
	
	/**
	 * Roda o analisador léxico
	 */
	private static void rodaLexico ( ) throws Exception
	{
		System.out.println ( "[" + arquivoEntrada + "]>>Rodando Lexico..." );
		
		boolean deuErro = false;
		
//		variaveis auxiliares
		int linhaAtual = -1;
		String tokens[];
		
		//leio ate o ultimo token
		while ( ( tokens = analizadorLexico.pegaToken() )[0].compareTo( "TO_EO" ) != 0 )
		{
			if ( analizadorLexico.pegaLinhaAtual() != linhaAtual )
			{
				linhaAtual = analizadorLexico.pegaLinhaAtual();
				
				//na primeira linha nao pulo linha
				if ( linhaAtual != 1 )
					escreveNoRelatorio( 0, "\n" );
				
				//escrevo saida no relatorio
				escreveNoRelatorio( 0, arquivoEntrada + ":" + linhaAtual + ":" );
			}				

			//escrevo token no relatorio
			escreveNoRelatorio ( 0, "[" + tokens[0] + "," + tokens[1] + "] " );
			
			//se deu erro escrevo na saida padrao!
			if ( tokens[0].compareTo( "TO_ER" ) == 0 )
			{
				deuErro = true;
				
				int erro = Integer.valueOf( tokens[1] ).intValue();;
				
				System.out.print ( "\n\tLinha " + linhaAtual + ": [" + tokens[0] + "," + tokens[1] + "]   " );
				
				switch ( erro )
				{				
					case 1:
						System.out.print ( "Nao achou o arquivo fonte" );
					break;
					
					case 2:
						System.out.print ( "Comentario nao fechado" );
					break;
					
					case 3:
						System.out.print ( "Comentario nao aberto" );
					break;
					
					case 4:
						System.out.print ( "Caractere invalido" );
					break;
					
					case 5:
						System.out.print ( "Identificador invalido" );
					break;
					
					case 6:
						System.out.print ( "Numero invalido" );
					break;
					
					case 7:
						System.out.println ( "Numero muito grande" );
					break;
				}
			}
									
		}
		
		//escrevo token EO no relatorio
		escreveNoRelatorio ( 0, "[" + tokens[0] + "," + tokens[1] + "] " );
		
		if ( deuErro )
			throw new Exception ( "Compilacao terminada (Erro lexico)" );
	}
	
	/**
	 * Modo de funcionamento completo... executando todas as fazes do compilador
	 */
	private static void modoCompleto ( ) throws Exception
	{		
		modoLexico ( );
		
		modoSintatico ( );
		
		//modoSemantico ( );
		
		modoGeraCodigo ( );
	}
	
	private static void modoGeraCodigo() throws Exception {
		carregaLexico ( );
		
		ArvorePrograma arvorePrograma;
		arvorePrograma = rodaSintatico ( );
		/*
		analizadorSemantico = new AnalizadorSemantico ( );
		analizadorSemantico.validaArvore  ( arvorePrograma );	
		*/
		String mepaFile = diretorioSaida + arquivoEntrada.substring(0, arquivoEntrada.length() - 3 ) + "mep";
		FileWriter arquivoMepa = new FileWriter ( mepaFile );
		
		geradorDeCodigo = new GeradorDeCodigo();
		ArrayList<String> codigo = geradorDeCodigo.geraCodigo( arvorePrograma );
		while ( codigo.size() != 0 )
			arquivoMepa.write( codigo.remove(0) + "\n" );
		
		arquivoMepa.close();
	}

	/**
	 * Modo de funcionamento onde somente e executado o analizador lexico!
	 */
	private static void modoLexico ( ) throws Exception
	{
		carregaLexico ( );
		rodaLexico ( );
	}

	/**
	 * Modo de funcionamento onde somente e executado o analizador semantico!
	 */
	private static void modoSemantico ( ) throws Exception
	{
		//System.out.println ( "Modo Semantico ainda nao implementado!" );
		
		carregaLexico ( );
		
		ArvorePrograma arvorePrograma;
		arvorePrograma = rodaSintatico ( );
		
		analizadorSemantico = new AnalizadorSemantico ( );
		analizadorSemantico.validaArvore  ( arvorePrograma );		
	}
	
	

	/**
	 * Roda o passo sintatico da compilação
	 * @throws Exception em caso de erro!
	 */
	private static ArvorePrograma rodaSintatico ( ) throws Exception
	{
		System.out.println ( "[" + arquivoEntrada + "]>>Carregando Sintatico..." );
		
		ArvorePrograma arvorePrograma;
		analizadorSintatico = new AnalizadorSintatico ( analizadorLexico );
		
		System.out.println ( "[" + arquivoEntrada + "]>>Rodando Sintatico..." );
				
		arvorePrograma = analizadorSintatico.pegaArvorePrograma();
		
		PercursoArvore percurso = new PercursoArvore ( arvorePrograma );
		
		if ( percursoInterativoAP )
			percurso.percursoInterativo ( );
		
		percurso.percursoNivel ( );				
		escreveNoRelatorio ( 1, percurso.pegaRelatorio ( ) );
		
		return arvorePrograma;
				
	}
	
	/**
	 * Modo de funcionamento onde somente e executado o analizador sintatico!
	 */
	private static void modoSintatico ( ) throws Exception
	{
		carregaLexico ( );
		rodaSintatico ( );			
	}
	
}

