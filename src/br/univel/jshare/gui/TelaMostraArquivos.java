package br.univel.jshare.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

public class TelaMostraArquivos extends JFrame {

	private static final long serialVersionUID = 8381402310343106264L;
	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaMostraArquivos frame = new TelaMostraArquivos();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TelaMostraArquivos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JButton btnCarregar = new JButton("Carregar");
		btnCarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				carregar();
			}
		});
		contentPane.add(btnCarregar, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					mostraSelecionadoTabela();
				}
			}
		});
	}

	protected void mostraSelecionadoTabela() {
		int linhaSelecionada = table.getSelectedRow();
		if (linhaSelecionada < 0) {
			System.out.println("Nenhuma selecionada");
		} else {
			int row = table.convertRowIndexToModel(linhaSelecionada);
			Arquivo arq = ((ResultadoModel) table.getModel()).getMeuItem(row);
			System.out.println(arq.getId());

		}
	}

	/**
	 * 
	 */
	protected void carregar() {
		Map<Cliente, List<Arquivo>> dados = gerarDados();

		ResultadoModel modelo = new ResultadoModel(dados);

		table.setModel(modelo);

	}

	private Map<Cliente, List<Arquivo>> gerarDados() {

		Map<Cliente, List<Arquivo>> dados = new HashMap<>();

		for (int c = 1; c <= 100; c++) {

			Cliente cli = new Cliente();
			cli.setId(c);
			cli.setNome("Cliente " + c);

			List<Arquivo> lista = new ArrayList<>();
			for (int a = 1; a <= 100; a++) {
				Arquivo arq = new Arquivo();
				arq.setId(1);
				arq.setNome("Arquivo " + a);

				lista.add(arq);
			}

			dados.put(cli, lista);
		}

		return dados;
	}

}
