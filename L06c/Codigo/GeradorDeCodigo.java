package Codigo;

import java.util.ArrayList;
import java.util.TreeMap;

import Sintatico.ArvorePrograma;

public class GeradorDeCodigo {
	
	/**
	 * Linhas de codigo mepa! lol
	 */
	ArrayList<String> codigo;
	
	/**
	 * Mapeia nome -> area na memoria
	 */
	TreeMap<String, Integer> variaveis;
	
	/**
	 * Mapeia nome -> tamanho
	 */
	TreeMap<String, Integer> tipos;
	
	/**
	 * Armazena ponteiros para as expressoes das constantes
	 */
	TreeMap<String, ArvorePrograma> constantes;
	
	int ultimoRotulo;
	int topoMemoria;
	
	public ArrayList<String> geraCodigo ( ArvorePrograma no ) 
	{
		//inicio a lista de codigos
		codigo = new ArrayList<String>();
		variaveis = new TreeMap<String, Integer>();
		constantes = new TreeMap<String, ArvorePrograma>();
		tipos = new TreeMap<String, Integer>();
		ultimoRotulo = 0;		
		topoMemoria = -1;
		
		//inicio o programa
		codigo.add("INPP");
		
		declaracoes( no.filho.irmao.filho );
		comando_composto ( no.filho.irmao.filho.irmao );		
				
		return codigo;
	}	

	/**
	 * Usada para gerar um nome de rotulo sem repetir! 
	 * @return
	 */
	private String novoRotulo() {
		return Integer.toString( ultimoRotulo++ );		
	}

	private void declaracoes (ArvorePrograma no)  
	{	
		no = no.filho;		
						
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
				
	}

	private void parte_vars(ArvorePrograma no) {
		no = no.filho;
		
		do
		{
			decl_vars ( no );
			no = no.irmao;
		}		
		while ( no != null );	
		
	}

	private void decl_vars(ArvorePrograma no) {
		no = no.filho;
				
		//nomes das variaveis
		ArvorePrograma identificador = no.filho;
		while ( identificador != null )
		{
			//aloco
			int endereco = alocaNovoEnderecoMemoria ( );
						
			//registra
			variaveis.put(identificador.valor, endereco);
								
			//proxima variavel
			identificador = identificador.irmao;
		}		
		
	}

	private int alocaNovoEnderecoMemoria() {
		
		//aloco
		topoMemoria++;
		codigo.add( "AMEM 1" );
		
		//retorno endereco
		return topoMemoria;
	}

	private void parte_tipos(ArvorePrograma no) {
		no = no.filho;
		
		do
		{
			definicao_tipos ( no );
			no = no.irmao;			
			
		}		
		while ( no != null );		
	}

	private void definicao_tipos(ArvorePrograma no) {
		
		//pego o nome da delcaracao
		no = no.filho;
		String identificador = no.valor;
				
		//declaro sempre com 1 byte!
		tipos.put(identificador, 1);		
	}

	private void parte_const ( ArvorePrograma no )
	{
		no = no.filho;
		
		do
		{
			decl_cte ( no );
			no = no.irmao;
		}		
		while ( no != null );	
	}
	

	private void decl_cte(ArvorePrograma no) {
		
		String identificador = no.filho.valor;
		ArvorePrograma expressao = no.filho.irmao;
		
		constantes.put(identificador, expressao);		
	}

	private void comando_composto(ArvorePrograma no)
	{
		no = no.filho;
		
		while ( no != null )
		{
			comando ( no );
			no = no.irmao;
		}				
	}

	private void comando(ArvorePrograma no) 
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

	private void comando_sem_rotulo(ArvorePrograma no) 
	{		
		no = no.filho;

		if ( no.nomeNo.compareTo("comando_sem_rotulo_identificador") == 0 )
			comando_sem_rotulo_identificador ( no );
		
		else if ( no.nomeNo.compareTo("comando_composto") == 0 )
			comando_composto ( no );

		else if ( no.nomeNo.compareTo("comando_condicional") == 0 )
			comando_condicional ( no );

		else if ( no.nomeNo.compareTo("comando_for") == 0 )
			comando_for ( no );

		else if ( no.nomeNo.compareTo("comando_while") == 0 )
			comando_while ( no );
	}

	private void comando_while(ArvorePrograma no) 
	{
		no = no.filho;
		
		String inicioWhile = novoRotulo();
		String fimWhile = novoRotulo();
		
		//inicio do while
		codigo.add( inicioWhile + " NADA" );
		expressao(no);
		
		//testo se terminou
		codigo.add( "DSVF " + fimWhile );
		
		//rodo o comando
		no = no.irmao;
		comando_composto(no);
		
		codigo.add( "DSVS " + inicioWhile );
		
		codigo.add( fimWhile + " NADA" );
	}

	private void comando_sem_rotulo_identificador(ArvorePrograma no) 
	{
		//pego nome da variavel
		ArvorePrograma identificador = no.filho;
		
		//rodo expressao
		expressao( no.filho.irmao );		
		
		//faço a atribuicao
		atribuicao ( identificador );
	}

	private void atribuicao(ArvorePrograma identificador) {
		
		int endereco = variaveis.get(identificador.valor);
				
		codigo.add( "ARMZ " + endereco );
	}
	
	private void chamadaVariavel(ArvorePrograma variavel) {
		
		int endereco = variaveis.get(variavel.valor);
		
		codigo.add( "CRVL " + endereco );
		
	}

	private void comando_for(ArvorePrograma no) {
		
		String rotuloiniciofor = novoRotulo();
		String rotulocorpofor = novoRotulo();
		String rotulofimfor = novoRotulo();

		expressao(no.filho.irmao);
		atribuicao(no.filho);

		codigo.add(rotuloiniciofor+"NADA ");
		//segunda expressao
		expressao(no.filho.irmao.irmao);
		//primeira expressao
		expressao(no.filho.irmao);
		codigo.add("      CMAG ");
		codigo.add("      DSVF "+rotulocorpofor);
		expressao(no.filho.irmao.irmao);
		chamadaVariavel(no.filho);
		codigo.add("      CMAG ");
		codigo.add("      DSVF "+rotulofimfor);
		//comandos dentro do for
		comando_sem_rotulo(no.filho.irmao.irmao.irmao);
		chamadaVariavel(no.filho);
		codigo.add("      CRCT 1");
		codigo.add("      SOMA");
		atribuicao(no.filho);
		codigo.add("      DSVS "+rotuloiniciofor);
		codigo.add(rotulocorpofor+"NADA ");
		expressao(no.filho.irmao.irmao);
		chamadaVariavel(no.filho);
		codigo.add("      CMEG ");
		codigo.add("      DSVF "+rotulofimfor);
		//comandos dentro do for
		comando_sem_rotulo(no.filho.irmao.irmao.irmao);
		chamadaVariavel(no.filho);
		codigo.add("      CRCT 1");
		codigo.add("      SUBT");
		atribuicao(no.filho);
		codigo.add("      DSVS " + rotuloiniciofor);
		codigo.add(rotulofimfor+"NADA ");

	}

	

	private void comando_condicional(ArvorePrograma no) 
	{
		no = no.filho;
		
		//gero os rotulos
		String rotuloelse = novoRotulo();
		String rotulofim = novoRotulo();
		
		//IF gero a expressao
		expressao ( no );			
		codigo.add("      DSVF " + rotuloelse );

		//THEN
		no = no.irmao;	
		comando_sem_rotulo( no );		
		codigo.add("      DSVS " + rotulofim );
		
		//ELSE
		no = no.irmao;
		codigo.add( rotuloelse + "NADA");			
		if ( no != null )
		{
			//se existe o else
			comando_sem_rotulo( no );
		}	
		
		codigo.add( rotulofim + "NADA");		
	}

	private void expressao(ArvorePrograma no) 
	{
		no = no.filho;
		
		//gero a primeira expressao
		expressao_simples ( no );		
			
		//verifico se não possui relacao
		no = no.irmao; 		
		if ( no == null )
			return;
		
		//pego o operador da relacao
		ArvorePrograma noRelacao = no;
		
		//gero segunda expressao
		no = no.irmao;
		expressao_simples ( no );
		
		//opero
		relacao ( noRelacao );
	}

	private void expressao_simples(ArvorePrograma no) 
	{
		no = no.filho;
		
		//verifico se tem pre-operador
		ArvorePrograma preOperador = null;		
		if ( no.nomeNo.compareTo("operador") == 0)
		{
			preOperador = no.filho;
			no = no.irmao;
		}
		
		//gero primeiro termo
		termo ( no );
		
		//faço inversao
		if ( preOperador != null && preOperador.valor.compareTo("OP_MENOS") == 0 )
		{			
			codigo.add ("INVR");
		}

		no = no.irmao;
		while ( no != null )
		{
			//pego o operador
			ArvorePrograma operador = no;
			
			//carrego proximo termo
			no = no.irmao;
			termo ( no );
			
			//aplico operacao (resultado no -1)
			operacaoSomaSubOr ( operador );
			
			//desaloco o topo
			codigo.add ("DESALOCA 1");
			
			no = no.irmao;
		}
		
		return;
	}

	private void operacaoSomaSubOr(ArvorePrograma operador) {
		
		String nomeOperacao = operador.valor;
		
		if ( nomeOperacao.compareTo("OP_MAIS") == 0 )
			codigo.add( "SOMA" );
		
		else if ( nomeOperacao.compareTo("OP_MENOS") == 0 )
			codigo.add( "SUBT" );

		else if ( nomeOperacao.compareTo("PA_OR") == 0 )
			codigo.add( "DISJ" );		
	}

	private void termo(ArvorePrograma no) 
	{
		//gero o fator
		no = no.filho;
		fator ( no );
		
		no = no.irmao;
		while ( no != null )
		{
			//pego operacao
			ArvorePrograma operacao = no;
			
			//rodo proximo fator
			no = no.irmao;
			fator ( no );
			
			//aplico operacao
			operacaoMultiDivRestoAnd( operacao );
			
			//desaloco o topo
			codigo.add ("DESALOCA 1");
			
			no = no.irmao;
		}
		
		return;
	}

	/**
	 * @todo implementar resto
	 * @param operacao
	 */
	private void operacaoMultiDivRestoAnd(ArvorePrograma operacao) {

		String nomeOperacao = operacao.valor;
		
		if ( nomeOperacao.compareTo("OP_VEZES") == 0 )
			codigo.add( "MULT" );
		
		else if ( nomeOperacao.compareTo("OP_DIV") == 0 )
			codigo.add( "DIVI" );

		else if ( nomeOperacao.compareTo("OP_RESTO") == 0 )
			codigo.add( "DIVI" );
		
		else if ( nomeOperacao.compareTo("PA_AND") == 0 )
			codigo.add( "CONJ" );		
	}

	private void fator(ArvorePrograma no) 
	{
		no = no.filho;
		
		//variavel
		if ( no.nomeNo.compareTo( "fator_identificador" ) == 0 )
			fator_identificador ( no );
	
		//outra expressao
		else if ( no.nomeNo.compareTo( "expressao" ) == 0 )
			expressao ( no );
		
		//eh um numero (constante)
		else if ( no.nomeNo.compareTo( "numero" ) == 0 )
			codigo.add( "CRCT " + no.valor );
		
		else
		{
			//do contrario EH >> NOT fator
			fator ( no );
			
			//aplico operador
			codigo.add("NEGA");
		}
	}

	private void fator_identificador(ArvorePrograma no) {
		
		//pego o nome
		no = no.filho;		
		String nomeVariavel = no.valor;
		
		//Verifico se é constante
		if ( constantes.containsKey(nomeVariavel) )
		{
			//pego ponteiro para a expressao
			ArvorePrograma noExpressao = constantes.get(nomeVariavel);
			
			//gero expressao
			expressao ( noExpressao );
		}
		else
		{
			//pego a area da memoria
			int endereco = variaveis.get (nomeVariavel);
			
			//copio os dados
			codigo.add ( "CRVL " + endereco );			
		}
	}

	private void relacao(ArvorePrograma no) 
	{		
		String nomeRelacao = no.valor;
		
		if ( nomeRelacao.compareTo("OP_IGUAL") == 0 )
			codigo.add( "CMIG" );
		
		else if ( nomeRelacao.compareTo("OP_DIFERE") == 0 )
			codigo.add( "CMDG" );

		else if ( nomeRelacao.compareTo("OP_MAIOR") == 0 )
			codigo.add( "CMMA" );

		else if ( nomeRelacao.compareTo("OP_MENOR") == 0 )
			codigo.add( "CMME" );

		else if ( nomeRelacao.compareTo("OP_MENORIG") == 0 )
			codigo.add( "CMEG" );

		else if ( nomeRelacao.compareTo("OP_MAIORIG") == 0 )
			codigo.add( "CMAG" );		
		
	}


	
	
}
