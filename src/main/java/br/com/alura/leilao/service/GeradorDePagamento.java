package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Pagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class GeradorDePagamento {

  private PagamentoDao pagamentos;
  private Clock clock;

  @Autowired
  public GeradorDePagamento(PagamentoDao pagamentos,Clock clock) {
    this.pagamentos = pagamentos;
    this.clock = clock;

  }

  public void gerarPagamento(Lance lanceVencedor) {
    LocalDate vencimento = gerarDataVencimento(LocalDate.now(clock));
    Pagamento pagamento = new Pagamento(lanceVencedor, vencimento);
    this.pagamentos.salvar(pagamento);
  }

  private LocalDate gerarDataVencimento(LocalDate data) {
    if (data.getDayOfWeek() == DayOfWeek.FRIDAY) {
      return data.plusDays(3);
    } else if (data.getDayOfWeek() == DayOfWeek.SATURDAY) {
      return data.plusDays(2);
    } else {
      return data.plusDays(1);
    }
  }
}
