package Sintatico;

/**
 * Representa um nó na arvore de programa
 */
public class ArvorePrograma 
{
	/**
	 * Ponteiro para o pai
	 */
	public ArvorePrograma pai;
	
	/**
	 * Ponteiro para o filho
	 */
	public ArvorePrograma filho;
	
	/**
	 * Ponteiro para o irmao
	 */
	public ArvorePrograma irmao;
	
	/**
	 * Identificado deste nó
	 */
	public int numeroNo;
	
	/**
	 * Representa qual produção da gramatica ele é
	 */
	public String nomeNo;
	
	/**
	 * Quando necessario, temos o valor
	 */
	public String valor;
	
	/**
	 * Construtor padrao
	 * 
	 * @param _numeroNo numero de identificacao
	 * @param _nomeNo nome
	 */
	public ArvorePrograma ( int _numeroNo, String _nomeNo )
	{
		numeroNo = _numeroNo;
		nomeNo = _nomeNo;
	}
	
	/**
	 * Adiciona um filho a este nó atualizando todos os ponteiros
	 * 
	 * @param _filho novo filho
	 */
	public void adicionaFilho ( ArvorePrograma _filho )
	{
		//seto eu como o pai do filho
		_filho.pai = this;
		
		//se não possuo nenhum filho
		if ( filho == null )		
			filho = _filho;
		
		//do contrario seto como irmao do meu primeiro filho
		else
		{
			ArvorePrograma caminha = filho;
			
			//caminho até encotrar um irmao vazio
			while ( caminha.irmao != null )
				caminha = caminha.irmao;
	
			caminha.irmao = _filho;
		}
	}	
	
	/**
	 * Retorna a quantidade de filhos que este nó possui
	 * 
	 * @return quantidade
	 */
	public int pegaQuantidadeFilhos ( )
	{
		ArvorePrograma tempFilho = filho;
		int cont = 0;
		
		while ( tempFilho != null )
		{
			tempFilho = tempFilho.irmao;
			cont++;
		}
		
		return cont;
	}
	
	/**
	 * Retorna vetor contendo todos os filhos
	 * 
	 * @return vetor de filhos
	 */
	public ArvorePrograma[] pegaFilhos ( )
	{
		ArvorePrograma[] retorno = new ArvorePrograma[pegaQuantidadeFilhos( )];
		
		if ( filho != null ) 
		{
			ArvorePrograma tempFilho = filho;
			int cont = 0;
			
			while ( tempFilho != null )
			{
				retorno[cont++] = tempFilho;
				tempFilho = tempFilho.irmao;
			}
		}
		return retorno;
	}
	
	/**
	 * Produz uma string formatada com a descrição deste nó
	 * 
	 * @return string
	 */
	public String pegaDescricao ( )
	{				
		String numNome = "[" + numeroNo + "](" + nomeNo + ")";
		String saidaIrmao;
		String saidaValor;
		String saidaFilho;
				
		if ( irmao == null )
			saidaIrmao = "(prox: NIL)";		
		else
			saidaIrmao = "(prox:" + irmao.numeroNo + ")";
					
		if ( valor != null )
			saidaValor = "\n(valor:" + valor + ")";
		else
			saidaValor = "";
		
		saidaFilho = "\n(filho: ";
		if ( filho == null )
			saidaFilho = saidaFilho + "NIL";			
		else
		{
			ArvorePrograma tempFilho = filho;
		
			saidaFilho += tempFilho.numeroNo;
			tempFilho = tempFilho.irmao;
			
			while ( tempFilho != null )
			{
				saidaFilho += "," + tempFilho.numeroNo;
				tempFilho = tempFilho.irmao;
			}
		}
		saidaFilho += ")";
		
		return numNome + saidaIrmao + saidaValor + saidaFilho;
	}
}
