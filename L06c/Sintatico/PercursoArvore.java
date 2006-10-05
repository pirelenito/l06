package Sintatico;

import java.io.IOException;
import Dados.Queue;

/**
 *  Classe que realiza o percurso em uma arvore de programa
 */
public class PercursoArvore 
{
	/**
	 * Nó da raiz da arvore
	 */
	private ArvorePrograma raiz;
	
	/**
	 * Construtor padrão
	 * 
	 * @param _raiz no raiz da arvore a percorrer
	 */
	public PercursoArvore ( ArvorePrograma _raiz )
	{
		raiz = _raiz;
	}
	
	/**
	 * Instancia de uma fila usada no percurso em nível
	 */
	private Queue fila;
	
	/**
	 * String utilizada para concatenar a saida do percurso em nivel
	 */
	private String relatorio;
	
	/**
	 * Função interna que le um nó e enfileira seus filhos
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
	 * Após a execução do modo interativo um relatorio é gerado
	 * 
	 * @return retorna o relatorio formatado para impressao
	 */
	public String pegaRelatorio ( )
	{
		return relatorio;
	}
	
	/**
	 * Modo de execução, onde é feito o percurso em nível da arvore
	 */
	public void percursoNivel ( )
	{			
		relatorio = new String( );
		
		fila = new Queue ( );
		
		//insiro a raiz na fila
		fila.enQueue ( raiz );
		
		//rodo o algoritimo até q não haja mais nós na fila
		while ( !fila.isEmpty() )
			percorreNivel ( (ArvorePrograma) fila.deQueue() );
		
	}
	
	/**
	 * Modo de execução, onde o usuario pode inferir aonde quer enxergar!
	 * 
	 * Comandos:
	 * 'f': mostra primeiro filho
	 * 'p': mostra próximo irmão
	 * 'v': volta para o nó pai
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
