package Semantico;

public class Tipo {
	
	public static final int TIPO_BOOLEANO = 0;
	public static final int TIPO_INTEIRO = 1;
	
	public static final int TIPO_FUNCAO = 2;
	public static final int TIPO_PROCEDIMENTO = 3;
	
	public static final int TIPO_ARRAY = 4;
	public static final int TIPO_REGISTRO = 5;
	public static final int TIPO_ROTULO = 6;
	
	public static final int TIPO_TIPO = 7;
	public static final int TIPO_PROGRAMA = 8;
	
	/**
	 * Qual o tipo
	 */
	public int tipo;
	
	public Tipo ( int _tipo )
	{
		tipo = _tipo;		
	}
		
}
