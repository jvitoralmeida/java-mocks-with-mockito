package br.com.alura.leilao;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.EnviadorDeEmails;
import br.com.alura.leilao.service.FinalizarLeilaoService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinalizarLeilaoServiceTest {

  @Mock private LeilaoDao leilaoDao;

  @Mock private EnviadorDeEmails enviadorDeEmails;

  private FinalizarLeilaoService finalizarLeilaoService;

  @BeforeEach
  public void beforeEach() {
    MockitoAnnotations.initMocks(this);
    this.finalizarLeilaoService = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
  }

  @Test
  public void deveriaFinalizarUmLeilao() {
    List<Leilao> leiloes = leiloes();
    //Metodo para mockar um dado quando o método buscarLeiloesExpirados() for chamado, então retorna um dado ficticio
    Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
    finalizarLeilaoService.finalizarLeiloesExpirados();
    Lance lanceVencedor = leiloes.get(0).getLanceVencedor();

    //Fazer assertivas
    Assert.assertEquals(new BigDecimal("900"), lanceVencedor.getValor());
    Assert.assertTrue(leiloes.get(0).isFechado());

    //Metodo para verificar se o método salvar com o primeiro leilao foi chamado
    Mockito.verify(leilaoDao).salvar(leiloes.get(0));
    //Metodo para verificar se o método enviarEmailVencedorLeilao com o lance vencedor foi chamado
    Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
  }

  @Test
  public void naoDeveriaDispararEmailQuandoUmaExceptionForLancadaAoSalvar() {
    List<Leilao> leiloes = leiloes();
    Leilao leilao = leiloes.get(0);

    Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
    Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);

    try {
      finalizarLeilaoService.finalizarLeiloesExpirados();
      //Método para verificar se esse mock não foi chamado, quando houver uma exception
      Mockito.verifyNoInteractions(enviadorDeEmails);
    } catch (Exception e) {
    }
  }

  private List<Leilao> leiloes() {
    List<Leilao> lista = new ArrayList<>();

    Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

    Lance primeiro = new Lance(new Usuario("Beltrano"), new BigDecimal("600"));
    Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

    leilao.propoe(primeiro);
    leilao.propoe(segundo);

    lista.add(leilao);

    return lista;
  }
}
