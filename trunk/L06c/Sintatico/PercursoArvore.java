package Sintatico;

import java.io.IOException;
import Dados.Queue;

/**
 *  Classe que realiza o percurso em uma arvore de programa
 */
public class PercursoArvore 
{
	/**
	 * N� da raiz da arvore
	 */
	private ArvorePrograma raiz;
	
	/**
	 * Construtor padr�o
	 * 
	 * @param _raiz no raiz da arvore a percorrer
	 */
	public PercursoArvore ( ArvorePrograma _raiz )
	{
		raiz = _raiz;
	}
	
	/**
	 * Instancia de uma fila usada no percurso em n�vel
	 */
	private Queue fila;
	
	/**
	 * String utilizada para concatenar a saida do percurso em nivel
	 */
	private String relatorio;
	
	/**
	 * Fun��o interna que le um n� e enfileira seus filhos
	 * 
	 * @param no atual
	 */
	private void percorreNivel ( ArvorePrograma no )
	{
		//System.out.println ( no.pegaDescricao ( ) );
		relatorio += no.pegaDescricao() + "\n\n";
		ArvorePrograma filhos[] = no.pegaFilhos();
		
		if ( filhos != null)
			for ( int i = 0; i < filhos.length; i++ )
			{
				fila.enQueue ( filhos[i] );
			}
	}
	
	/**
	 * Ap�s a execu��o do modo interativo um relatorio � gerado
	 * 
	 * @return retorna o relatorio formatado para impressao
	 */
	public String pegaRelatorio ( )
	{
		return relatorio;
	}
	
	/**
	 * Modo de execu��o, onde � feito o percurso em n�vel da arvore
	 */
	public void percursoNivel ( )
	{			
		relatorio = new String( );
		
		fila = new Queue ( );
		
		//insiro a raiz na fila
		fila.enQueue ( raiz );
		
		//rodo o algoritimo at� q n�o haja mais n�s na fila
		while ( !fila.isEmpty() )
			percorreNivel ( (ArvorePrograma) fila.deQueue() );
		
	}
	
	/**
	 * Modo de execu��o, onde o usuario pode inferir aonde quer enxergar!
	 * 
	 * Comandos:
	 * 'f': mostra primeiro filho
	 * 'p': mostra pr�ximo irm�o
	 * 'v': volta para o n� pai
	 * 's': finaliza o percurso
	 */
	public void percursoInterativo ( ) 
	{		
		int lido = 0;
		ArvorePrograma temp = raiz;
		System.out.println ( raiz.pegaDescricao() );
		
		while ( lido != 's' )
		{
			try
			{
				lido = System.in.read();
			}
			catch ( Exception e ){}
			
			if( lido == 'f' )
				if ( temp.filho != null )
				{
					temp = temp.filho;
					System.out.println ( temp.pegaDescricao() );
				}
			if ( lido == 'p' )
				if ( temp.pai != null )
				{
					temp = temp.pai;
					System.out.println ( temp.pegaDescricao() );
				}
			if ( lido == 'v' )
				if ( temp.irmao != null )
				{
					temp = temp.irmao;
					System.out.println ( temp.pegaDescricao() );
				}
		}		
	}
}
