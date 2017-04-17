package br.univel.jshare.comum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.univel.jshare.gui.main.TelaPrincipal;

public class Servidor implements IServer {

	// Armazenar os clientes conectados e a lista de arquivos
	private Map<Cliente, List<Arquivo>> mapa = new HashMap<Cliente, List<Arquivo>>();

	@Override
	public void registrarCliente(Cliente c) throws RemoteException {
		TelaPrincipal.setLog(c.getNome().concat(" acabou de conectar com o IP ").concat(c.getIp()));
	}

	@Override
	public void publicarListaArquivos(Cliente cliente, List<Arquivo> lista) throws RemoteException {
		// Armazena o cliente e os seus arquivos no servidor
		mapa.put(cliente, lista);
	}

	@Override
	public Map<Cliente, List<Arquivo>> procurarArquivo(String query, TipoFiltro tipoFiltro, String filtro)
			throws RemoteException {
		// Limpa as variáveis de armazenamento
		Map<Cliente, List<Arquivo>> mapaPesquisa = new HashMap<Cliente, List<Arquivo>>();

		// De acordo com a classe br.univel.jshare.busca.Busca
		Pattern pat = Pattern.compile(".*" + query + ".*");

		// Percorre a lista local com os clientes e adiciona somente os arquivos
		// que atendem o filtro
		for (Entry<Cliente, List<Arquivo>> e : mapa.entrySet()) {
			Cliente cliente = e.getKey();
			List<Arquivo> arquivos = new ArrayList<>();
			for (Arquivo arquivo : e.getValue()) {

				Matcher pesquisa;

				switch (tipoFiltro) {
				case NOME:
					pesquisa = pat.matcher(arquivo.getNome().toLowerCase());
					if (pesquisa.matches()) {
						arquivos.add(arquivo);
					}
					break;

				case EXTENSAO:
					pesquisa = pat.matcher(arquivo.getExtensao().toLowerCase());
					if (pesquisa.matches()) {
						arquivos.add(arquivo);
					}
					break;

				case TAMANHO_MAX:
					if (arquivo.getTamanho() <= Long.parseLong(query)) {
						arquivos.add(arquivo);
					}
					break;

				case TAMANHO_MIN:
					if (arquivo.getTamanho() >= Long.parseLong(query)) {
						arquivos.add(arquivo);
					}
					break;
				}
			}
			mapaPesquisa.put(cliente, arquivos);
		}
		return mapaPesquisa;
	}

	@Override
	public byte[] baixarArquivo(Cliente cli, Arquivo arq) throws RemoteException {
		TelaPrincipal.adicionarTransferencia(cli, arq);
		return leia(new File(arq.getPath()));
	}

	@Override
	public void desconectar(Cliente c) throws RemoteException {
		Iterator<Map.Entry<Cliente, List<Arquivo>>> itr = mapa.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<Cliente, List<Arquivo>> entry = itr.next();
			if (entry.getKey().getId() == c.getId()) {
				itr.remove();
				TelaPrincipal
						.setLog(c.getNome().concat(" com o IP ").concat(c.getIp()).concat(" acabou de desconectar"));
			}
		}
	}

	// Ler um arquivo
	private byte[] leia(File arq) {
		Path path = Paths.get(arq.getPath());
		try {
			byte[] dados = Files.readAllBytes(path);
			return dados;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
