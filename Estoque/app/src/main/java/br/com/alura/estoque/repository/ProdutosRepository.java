package br.com.alura.estoque.repository;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ProdutosRepository {
    private final ProdutoDAO dao;
    private final ProdutoService service;

    public ProdutosRepository(ProdutoDAO dao) {
        this.dao = dao;
        service = new EstoqueRetrofit().getProdutoService();
    }

    public void buscaProdutos(DadosCarregadosListener<List<Produto>> listener) {
        buscaProdutosInternos(listener);
    }

    public void buscaProdutosInternos(DadosCarregadosListener<List<Produto>> listener) {
        new BaseAsyncTask<>(dao::buscaTodos,
                resultado -> {
                    //notifica que o dado está pronto
                    listener.quandoCarregados(resultado);
                    buscaProdutosNaApi(listener);
                })
                .execute();
    }

    private void buscaProdutosNaApi(DadosCarregadosListener<List<Produto>> listener) {

        Call<List<Produto>> call = service.buscaTodos();
        //notifica que o dados esta pronto
        new BaseAsyncTask<>(() -> {
            try {
                Response<List<Produto>> response = call.execute();
                List<Produto> produtosNovos = response.body();
                dao.salva(produtosNovos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dao.buscaTodos();

        }, listener::quandoCarregados).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void salva(Produto produto, DadosCarregadosCallback<Produto> callback) {
        salvaNaAPI(produto, callback);
    }

    private void salvaNaAPI(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = service.salva(produto);
        call.enqueue(new Callback<Produto>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Produto> call, Response<Produto> response) {
                if (response.isSuccessful()) {
                    Produto produto = response.body();
                    //notificar dado esta pronto
                    if (produto != null) {
                        salvaInterno(produto, callback);
                    }else {
                        //notificar falha
                        callback.quandoFalha("Resposta não sucedida");
                    }

                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Produto> call, Throwable t) {
                //notificar falha
                callback.quandoFalha("Falha de comunicação" + t.getMessage());
            }
        });
    }

    private void salvaInterno(Produto produtoSalvo, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produtoSalvo);
            return dao.buscaProduto(id);
        }, callback::quandoSucesso)
                .execute();
    }

    //notifica a activity quando dados foram carregados
    public interface DadosCarregadosListener<T> {
        void quandoCarregados(T resultado);
    }

    public interface DadosCarregadosCallback<T>{
        void quandoSucesso(T resultado);
        void quandoFalha(String erro);
    }
}