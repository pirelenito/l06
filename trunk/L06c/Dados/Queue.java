package Dados;


import java.util.*;  // this contains  the Vector class

/**
 * Implementação de uma fila simples
 */
public class Queue {

	/**
	 * Vector que armazena dados da fila
	 */
    private Vector queue;

    /**
     * Construtor padrão
     */
    public Queue ()  {
        queue = new Vector();
    }

    /**
     * Construtor
     * @param initialSize tamanho inicial (aumenta automaticamente)
     */
    public Queue (int initialSize) {
         if (initialSize >= 1) {
             queue = new Vector(initialSize);
         } else {
             queue = new Vector();
         }
     }

    /**
     * Enfileira um dado
     * @param item a enfileirar
     */
    public void enQueue (Object item) {
        queue.addElement(item);
    }

    /**
     * Leio o topo sem retirar da fila
     * @return topo
     */
    public Object front () {
        return queue.firstElement();
    }

    /**
     * Tiro o primeiro dado da fila
     * @return dado ou null se vazia
     */
    public Object deQueue () {
        Object obj = null;

       if (!queue.isEmpty()) {
            obj = front();
           queue.removeElement(obj);
       }

        return obj;
    }

    /**
     * Verifica se está vazia
     * @return se é verdade
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Retorno o tamaho da fila
     * @return tamanho
     */
    public int size() {
         return queue.size();
     }

    /**
     * Verifico quanto espaço ainda existe para inserção 
     * @return espaço disponivel
     */
     public int availableRoom() {
         return (queue.capacity() - queue.size());
     }

}