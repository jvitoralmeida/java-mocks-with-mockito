package br.com.alura.leilao;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.GeradorDePagamento;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class GeradorDePagamentoTest {

  //Annotation para informar o mockito que a classe deve ser mockada
  @Mock private PagamentoDao pagamentoDao;
  @Mock private Clock clock;

  //Classe utilitária pra capturar uma variável que é criada dentro de um método.
  @Captor private ArgumentCaptor<Pagamento> captor;

  private GeradorDePagamento geradorDePagamento;

  //Antes de cada teste faça algo
  @BeforeEach
  public void beforeEach() {
    //Classe utilitária para inicializar os mocks da classe que está sendo trabalhada(GeradorDePagamentoTest.class)
    MockitoAnnotations.initMocks(this);
    this.geradorDePagamento = new GeradorDePagamento(pagamentoDao, clock);
  }

  //Annotation pra definir um test
  @Test
  public void deveriaCriarPagamentoParaVencedorDoLeilao() {
    Leilao leilao = leilao();
    Lance lanceVencedor = leilao.getLanceVencedor();
    Usuario usuario = lanceVencedor.getUsuario();

    Instant instant = LocalDate.of(2021, 7, 5).atStartOfDay(ZoneId.systemDefault()).toInstant();
    Mockito.when(clock.instant()).thenReturn(instant);
    Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    geradorDePagamento.gerarPagamento(lanceVencedor);
    Mockito.verify(pagamentoDao).salvar(captor.capture());
    Pagamento pagamento = captor.getValue();

    Assert.assertEquals(pagamento.getLeilao(), leilao);
    Assert.assertEquals(pagamento.getUsuario(), usuario);
    Assert.assertEquals(lanceVencedor.getValor(), pagamento.getValor());
    Assert.assertEquals(pagamento.getVencimento(), LocalDate.now().plusDays(1));
  }

  private Leilao leilao() {

    Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

    Lance vencedor = new Lance(new Usuario("Beltrano"), new BigDecimal("600"));

    leilao.propoe(vencedor);
    leilao.setLanceVencedor(vencedor);

    return leilao;
  }
}
