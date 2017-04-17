package br.univel.jshare.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

public class ResultadoModel extends AbstractTableModel {

	private static final long serialVersionUID = 2523116764491700723L;

	private Object[][] matrix;

	/**
	 * Código precisa ser revisado caso haja um cliente (chave) com lista nula
	 * ou vazia, pois assume-se que todas as chaves tenham no mínimo um
	 * arquivo.
	 * 
	 * @param dados
	 */
	public ResultadoModel(Map<Cliente, List<Arquivo>> dados) {

		int tempCli = 0;
		for (Entry<Cliente, List<Arquivo>> e : dados.entrySet()) {
			if (e.getValue() != null) {
				tempCli += e.getValue().size();
			}
		}

		matrix = new Object[tempCli][4];

		List<Cliente> list = new ArrayList<>(dados.keySet());

		list.sort((o1, o2) -> o1.getNome().compareTo(o2.getNome()));

		int cont = 0;
		for (Cliente cli : list) {
			for (Arquivo arq : dados.get(cli)) {
				matrix[cont][0] = cli.getId();
				matrix[cont][1] = cli.getNome();
				matrix[cont][2] = arq.getId();
				matrix[cont][3] = arq.getNome();
				cont++;
			}
		}
	}

	@Override
	public int getColumnCount() {
		return matrix[0].length;
	}

	@Override
	public int getRowCount() {
		return matrix.length;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return matrix[arg0][arg1];
	}

	public Arquivo getMeuItem(int row) {
		Arquivo arq = new Arquivo();
		arq.setId(row);
		return arq;
	}

}
