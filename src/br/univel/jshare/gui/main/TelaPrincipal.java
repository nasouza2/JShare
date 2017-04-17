package br.univel.jshare.gui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import br.dagostini.exemplos.Md5Util;
import br.testes.MeuModelo;
import br.univel.jshare.comum.Arquivo;
import br.univel.jshare.comum.Cliente;
import br.univel.jshare.comum.IServer;
import br.univel.jshare.comum.Servidor;
import br.univel.jshare.comum.TipoFiltro;


public class TelaPrincipal extends JFrame {

	private static final long serialVersionUID = -2487884131998932737L;
	private boolean conectado = false;
	private Cliente cliente;
	private Registry registry;
	private IServer servidor;
	private Servidor servidorLocal;
	private static StringBuffer logTransferencia = new StringBuffer();
	private static StringBuffer log = new StringBuffer();
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	private JPanel contentPane;
	private static JTextField txfEndereco;
	private JTextField txfPorta;
	private JTextField txfPasta;
	private static JTextField txfNome;
	private JSplitPane splitPane;

	private static JTextArea textArea;
	private JTable tbArquivos;
	private MeuModelo meuModelo;
	private JTextField txfPesquisa;

	private JComboBox<TipoFiltro> cbFiltro;
	// private ComboBox<TipoFiltro> cbFiltro2;

	private JButton btnConectar;

	private static JTextArea taTransferencias;

	private JTabbedPane tabbedPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaPrincipal frame = new TelaPrincipal();
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
	public TelaPrincipal() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("src\\readicon.png"));
		setTitle("Compartilhador de Arquivos");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 776, 515);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		contentPane.add(splitPane, BorderLayout.CENTER);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Conexão", new ImageIcon("src\\icon_connect.gif"), panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.BLACK);
		panel_5.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.gridheight = 3;
		gbc_panel_5.insets = new Insets(5, 5, 5, 5);
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.gridx = 0;
		gbc_panel_5.gridy = 0;
		panel.add(panel_5, gbc_panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(new ImageIcon("src\\fileshare.png"));
		panel_5.add(lblNewLabel, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Funcionamento", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(5, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 0;
		panel.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_3.rowHeights = new int[] { 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		JRadioButton rdbtnServidor = new JRadioButton("Servidor e Cliente");
		rdbtnServidor.setSelected(true);
		GridBagConstraints gbc_rdbtnServidor = new GridBagConstraints();
		gbc_rdbtnServidor.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnServidor.gridx = 0;
		gbc_rdbtnServidor.gridy = 0;
		panel_3.add(rdbtnServidor, gbc_rdbtnServidor);

		JRadioButton rdbtnCliente = new JRadioButton("Apenas Cliente");
		GridBagConstraints gbc_rdbtnCliente = new GridBagConstraints();
		gbc_rdbtnCliente.anchor = GridBagConstraints.WEST;
		gbc_rdbtnCliente.gridx = 1;
		gbc_rdbtnCliente.gridy = 0;
		panel_3.add(rdbtnCliente, gbc_rdbtnCliente);

		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnServidor);
		bg.add(rdbtnCliente);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Parâmetros", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 1;
		gbc_panel_4.gridy = 1;
		panel.add(panel_4, gbc_panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel_4.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);

		JLabel lblNome = new JLabel("Nome");
		GridBagConstraints gbc_lblNome = new GridBagConstraints();
		gbc_lblNome.insets = new Insets(0, 0, 5, 5);
		gbc_lblNome.anchor = GridBagConstraints.EAST;
		gbc_lblNome.gridx = 0;
		gbc_lblNome.gridy = 0;
		panel_4.add(lblNome, gbc_lblNome);

		txfNome = new JTextField();
		GridBagConstraints gbc_txfNome = new GridBagConstraints();
		gbc_txfNome.gridwidth = 3;
		gbc_txfNome.insets = new Insets(0, 0, 5, 0);
		gbc_txfNome.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfNome.gridx = 1;
		gbc_txfNome.gridy = 0;
		panel_4.add(txfNome, gbc_txfNome);
		txfNome.setColumns(10);

		JLabel lblEndereo = new JLabel("Endereço");
		GridBagConstraints gbc_lblEndereo = new GridBagConstraints();
		gbc_lblEndereo.insets = new Insets(0, 0, 5, 5);
		gbc_lblEndereo.anchor = GridBagConstraints.EAST;
		gbc_lblEndereo.gridx = 0;
		gbc_lblEndereo.gridy = 1;
		panel_4.add(lblEndereo, gbc_lblEndereo);

		txfEndereco = new JTextField();
		GridBagConstraints gbc_txfEndereco = new GridBagConstraints();
		gbc_txfEndereco.insets = new Insets(0, 0, 5, 5);
		gbc_txfEndereco.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfEndereco.gridx = 1;
		gbc_txfEndereco.gridy = 1;
		panel_4.add(txfEndereco, gbc_txfEndereco);
		txfEndereco.setColumns(10);

		JLabel lblPorta = new JLabel("Porta");
		GridBagConstraints gbc_lblPorta = new GridBagConstraints();
		gbc_lblPorta.insets = new Insets(0, 0, 5, 5);
		gbc_lblPorta.anchor = GridBagConstraints.EAST;
		gbc_lblPorta.gridx = 2;
		gbc_lblPorta.gridy = 1;
		panel_4.add(lblPorta, gbc_lblPorta);

		txfPorta = new JTextField("");
		GridBagConstraints gbc_txfPorta = new GridBagConstraints();
		gbc_txfPorta.insets = new Insets(0, 0, 5, 0);
		gbc_txfPorta.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfPorta.gridx = 3;
		gbc_txfPorta.gridy = 1;
		panel_4.add(txfPorta, gbc_txfPorta);
		txfPorta.setColumns(10);

		JLabel lblPasta = new JLabel("Pasta");
		GridBagConstraints gbc_lblPasta = new GridBagConstraints();
		gbc_lblPasta.anchor = GridBagConstraints.EAST;
		gbc_lblPasta.insets = new Insets(0, 0, 0, 5);
		gbc_lblPasta.gridx = 0;
		gbc_lblPasta.gridy = 2;
		panel_4.add(lblPasta, gbc_lblPasta);

		txfPasta = new JTextField();
		GridBagConstraints gbc_txfPasta = new GridBagConstraints();
		gbc_txfPasta.gridwidth = 3;
		gbc_txfPasta.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfPasta.gridx = 1;
		gbc_txfPasta.gridy = 2;
		panel_4.add(txfPasta, gbc_txfPasta);
		txfPasta.setColumns(10);

		JButton btnFolder = new JButton("");
		btnFolder.setFocusable(false);
		btnFolder.setBorder(null);
		btnFolder.setMargin(new Insets(0, 0, 0, 0));
		btnFolder.setIconTextGap(0);
		btnFolder.setIcon(new ImageIcon("src\\dir.png"));

		txfPasta.setLayout(new BorderLayout());
		txfPasta.add(btnFolder, BorderLayout.EAST);

		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Campos preenchidos
				if (validarCampos()) {
					String nome = txfNome.getText().trim();
					String endereco = txfEndereco.getText().trim();
					int porta = Integer.parseInt(txfPorta.getText().trim());
					String caminho = txfPasta.getText().trim();

					// Conectado como servidor/cliente
					if (rdbtnServidor.isSelected()) {
						if (!conectado) {
							iniciaServidor(porta);
							conectar(nome, endereco, porta, caminho);
							conectado = true;
						} else {
							desconectar(cliente);
							desligarServidor();
							conectado = false;
						}
					} else
					// Conectado somente como cliente
					if (rdbtnCliente.isSelected()) {
						if (!conectado) {
							conectar(nome, endereco, porta, caminho);
							conectado = true;
						} else {
							desconectar(cliente);
							conectado = false;
						}
					}
					habilitarCampos();
				}
				// Caso os campos não sejam prenchidos
				else {
					JOptionPane.showMessageDialog(rootPane, "Por favor, preencha todos os campos!", "Aviso",
							JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});
		GridBagConstraints gbc_btnConectar = new GridBagConstraints();
		gbc_btnConectar.anchor = GridBagConstraints.NORTH;
		gbc_btnConectar.gridx = 1;
		gbc_btnConectar.gridy = 2;
		panel.add(btnConectar, gbc_btnConectar);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Busca", new ImageIcon("src\\search.png"), panel_1, null);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		txfPesquisa = new JTextField();
		txfPesquisa.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pesquisar();
			}
		});

		JLabel lblPesquisa = new JLabel("Pesquisa");
		GridBagConstraints gbc_lblPesquisa = new GridBagConstraints();
		gbc_lblPesquisa.gridwidth = 2;
		gbc_lblPesquisa.anchor = GridBagConstraints.WEST;
		gbc_lblPesquisa.insets = new Insets(0, 0, 5, 5);
		gbc_lblPesquisa.gridx = 0;
		gbc_lblPesquisa.gridy = 0;
		panel_1.add(lblPesquisa, gbc_lblPesquisa);

		JLabel lblTipoFiltro = new JLabel("Tipo Filtro");
		GridBagConstraints gbc_lblTipoFiltro = new GridBagConstraints();
		gbc_lblTipoFiltro.anchor = GridBagConstraints.WEST;
		gbc_lblTipoFiltro.insets = new Insets(0, 0, 5, 0);
		gbc_lblTipoFiltro.gridx = 2;
		gbc_lblTipoFiltro.gridy = 0;
		panel_1.add(lblTipoFiltro, gbc_lblTipoFiltro);
		GridBagConstraints gbc_txfPesquisa = new GridBagConstraints();
		gbc_txfPesquisa.gridwidth = 2;
		gbc_txfPesquisa.insets = new Insets(0, 0, 5, 5);
		gbc_txfPesquisa.fill = GridBagConstraints.HORIZONTAL;
		gbc_txfPesquisa.gridx = 0;
		gbc_txfPesquisa.gridy = 1;
		panel_1.add(txfPesquisa, gbc_txfPesquisa);
		txfPesquisa.setColumns(10);

		cbFiltro = new JComboBox<>(TipoFiltro.values());
		GridBagConstraints gbc_cbFiltro = new GridBagConstraints();
		gbc_cbFiltro.insets = new Insets(0, 0, 5, 0);
		gbc_cbFiltro.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbFiltro.gridx = 2;
		gbc_cbFiltro.gridy = 1;
		panel_1.add(cbFiltro, gbc_cbFiltro);

		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 3;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 2;
		panel_1.add(scrollPane_1, gbc_scrollPane_1);

		tbArquivos = new JTable();
		tbArquivos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					mostraSelecionadoTabela();
				}
			}
		});
		scrollPane_1.setViewportView(tbArquivos);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Transferências", new ImageIcon("src\\download.png"), panel_2, null);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JScrollPane scrollPane_2 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 0;
		gbc_scrollPane_2.gridy = 0;
		panel_2.add(scrollPane_2, gbc_scrollPane_2);

		taTransferencias = new JTextArea();
		taTransferencias.setEnabled(false);
		taTransferencias.setEditable(false);
		scrollPane_2.setViewportView(taTransferencias);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);

		textArea = new JTextArea();
		textArea.setForeground(new Color(50, 205, 50));
		textArea.setBackground(Color.BLACK);
		scrollPane.setViewportView(textArea);
		splitPane.setDividerLocation(200);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				relocatedivider();
			}
		});

		// Traz alguns dados por default
		valoresPadroes();

	}

	// Inicia o servidor
	private void iniciaServidor(int porta) {
		try {
			servidorLocal = new Servidor();
			IServer server = (IServer) UnicastRemoteObject.exportObject(servidorLocal, 0);
			registry = getRegistry(porta);
			registry.rebind(IServer.NOME_SERVICO, server);
			setLog("Servidor iniciado");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// Desliga o servidor
	private void desligarServidor() {
		try {
			registry.unbind(IServer.NOME_SERVICO);
			UnicastRemoteObject.unexportObject(servidorLocal, true);
			setLog("Servidor desligado");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	// Desconecta o cliente
	private void desconectar(Cliente cliente) {
		try {
			servidor.desconectar(cliente);
			servidor = null;
			// Limpa a lista de arquivos
			meuModelo = new MeuModelo(null);
			tbArquivos.setModel(meuModelo);
			// Limpa o log de transferências
			taTransferencias.setText("");
			setLog("Desconectado");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// Conecta a um servidor passando o IP, porta, o nome do cliente e o caminho
	// da pasta com meus arquivos
	private void conectar(String nome, String endereco, int porta, String caminho) {

		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(endereco, porta);
			servidor = (IServer) registry.lookup(IServer.NOME_SERVICO);

			// Cria o cliente
			cliente = new Cliente();
			int idRandomico = ThreadLocalRandom.current().nextInt(0, 2000 + 1);
			cliente.setId(idRandomico);
			cliente.setIp(lerIp());
			cliente.setNome(nome);
			cliente.setPorta(porta);

			// Registra o cliente no servidor
			servidor.registrarCliente(cliente);
			// Faz a leitura dos arquivos e publica
			servidor.publicarListaArquivos(cliente, lerArquivosPasta(caminho, cliente.getId()));
			setLog("Conectado com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IServer conectar(String endereco, int porta) {

		IServer server = null;
		try {
			Registry registry = LocateRegistry.getRegistry(endereco, porta);
			server = (IServer) registry.lookup(IServer.NOME_SERVICO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return server;

	}

	private Registry getRegistry(int porta) throws RemoteException {
		try {
			registry = LocateRegistry.createRegistry(porta);
		} catch (Exception e) {
			registry = LocateRegistry.getRegistry(porta);
		}
		return registry;
	}

	// Adiciona uma mensagem para apresentação no log
	public static void setLog(String mensagem) {
		log.append(sdf.format(new Date())).append(" ").append(mensagem).append("\n");
		textArea.setText(log.toString());
	}

	// Feito com base na classe br.dagostini.exemplos.LerIp
	// Retorna o IP, caso não consiga ele apresenta como Desconhecido
	private static String lerIp() {
		String IPString;
		try {
			InetAddress IP = InetAddress.getLocalHost();
			IPString = IP.getHostAddress();
		} catch (UnknownHostException e) {
			IPString = "Desconhecido";
		}
		return IPString;
	}

	// Feito com base na classe br.dagostini.exemplos.ListarDiretoriosArquivos
	// Faz a leitura dos arquivos de determinado caminho (Path)
	private List<Arquivo> lerArquivosPasta(String caminho, long id) {
		List<Arquivo> arquivosDisponiveis = new ArrayList<>();
		File arquivos = new File(caminho);
		for (File arquivo : arquivos.listFiles()) {
			// Verifica se é um arquivo e não um diretório
			if (arquivo.isFile()) {
				Arquivo arquivoAtual = new Arquivo();
				arquivoAtual.setId(id);
				arquivoAtual.setNome(pegarNomeArquivo(arquivo.getName()));
				arquivoAtual.setDataHoraModificacao(new Date(arquivo.lastModified()));
				arquivoAtual.setPath(arquivo.getPath());
				arquivoAtual.setTamanho(arquivo.length());
				arquivoAtual.setMd5(Md5Util.getMD5Checksum(arquivo.getAbsolutePath()));
				arquivoAtual.setExtensao(pegarExtensaoArquivo(arquivo.getName()));
				arquivosDisponiveis.add(arquivoAtual);
			}
		}
		return arquivosDisponiveis;
	}

	// Pesquisa os arquivos
	private void pesquisar() {
		if (servidor != null) {
			// Pega o texto digitado no campo pesquisa
			String query = txfPesquisa.getText().trim();
			// Pega o filtro especificado
			TipoFiltro tipoFiltro = (TipoFiltro) cbFiltro.getSelectedItem();

			Map<Cliente, List<Arquivo>> listaClientes = new HashMap<>();

			try {
				// Procura os arquivos que estão no servidor com os filtros
				// passados
				listaClientes = servidor.procurarArquivo(query, tipoFiltro, "");

				// Cria o modelo e adiciona os clientes
				meuModelo = new MeuModelo(listaClientes);
				tbArquivos.setModel(meuModelo);

			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}

	// Pega a extensão do arquivo
	public String pegarExtensaoArquivo(String nome) {
		String extensao = "";
		int i = nome.lastIndexOf('.');
		if (i > 0) {
			extensao = nome.substring(i);
		}
		return extensao;
	}

	// Pega o nome do arquivo
	public String pegarNomeArquivo(String nome) {
		int pos = nome.lastIndexOf(".");
		if (pos > 0) {
			nome = nome.substring(0, pos);
		}
		return nome;
	}

	// Feito conforme a classe br.univel.jshare.gui.TelaMostraArquivos
	private void mostraSelecionadoTabela() {
		int linhaSelecionada = tbArquivos.getSelectedRow();
		if (linhaSelecionada < 0) {
			JOptionPane.showMessageDialog(rootPane, "Nenhuma linha selecionada!");
		} else {
			int row = tbArquivos.convertRowIndexToModel(linhaSelecionada);

			try {
				Map<Cliente, Arquivo> informacoesArquivo = meuModelo.pegarInformacoesArquivo(row);

				for (Entry<Cliente, Arquivo> e : informacoesArquivo.entrySet()) {
					Arquivo arquivo = e.getValue();
					Cliente cliente = e.getKey();

					// Conecta no cliente do arquivo para baixar//
					IServer server = conectar(cliente.getIp(), cliente.getPorta());

					if (server != null) {
						byte[] dados = server.baixarArquivo(e.getKey(), arquivo);

						escreva(new File(txfPasta.getText().trim().concat("/Baixados/").concat(arquivo.getNome())
								.concat(arquivo.getExtensao())), dados);

						// Verifica se não é host para não duplicar o log de
						// transferências
						if (servidorLocal == null) {
							adicionarTransferencia(cliente, arquivo);
						}

					}

				}
			} catch (ParseException | RemoteException e) {
				JOptionPane.showMessageDialog(rootPane, "Erro ao baixar o arquivo.");
			}
		}
	}

	// Grava o arquivo
	public void escreva(File arq, byte[] dados) {
		try {
			Files.write(Paths.get(arq.getPath()), dados, StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	// Adiciona o log das transferências dos arquivos
	public static void adicionarTransferencia(Cliente cliente, Arquivo arquivo) {
		logTransferencia.append(sdf.format(new Date())).append(" ").append(txfNome.getText().trim())
				.append(" (".concat(TelaPrincipal.lerIp()).concat(") ")).append("baixou o arquivo ")
				.append(arquivo.getNome().concat(arquivo.getExtensao())).append(" de ").append(cliente.getNome())
				.append(" (".concat(cliente.getIp()).concat(") ")).append("\n");
		taTransferencias.setText(logTransferencia.toString());
		
	}


	// Desabilita e habilita os campos se estiver conectado ou desconectado
	private void habilitarCampos() {
		txfNome.setEnabled(!conectado);
		txfEndereco.setEnabled(!conectado);
		txfPorta.setEnabled(!conectado);
		txfPasta.setEnabled(!conectado);
		btnConectar.setText(conectado ? "Desconectar" : "Conectar");
	}

	// Valida a entrada dos campos
	private boolean validarCampos() {
		return txfEndereco.getText().trim().length() > 0 && txfPorta.getText().trim().length() > 0
				&& txfNome.getText().trim().length() > 0 && txfPasta.getText().trim().length() > 0;
	}

	// Valores defaults para preenchimento dos campos de conexão
	private void valoresPadroes() {
		// Pode ser o IP real ou localhost
		String IP = lerIp();
		String porta = "1099";
		String nome = "Cliente";
		String caminho = "." + File.separatorChar;

		txfNome.setText(nome);
		txfEndereco.setText(IP);
		txfPorta.setText(porta);
		txfPasta.setText(caminho);
	}

	protected void relocatedivider() {
		splitPane.setDividerLocation(this.getHeight() - 150);		
	}
	
	// Para ficar mais bonitinho, encontrei um metódo para mostrar a extensão do arquivo:
	// http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}


}
