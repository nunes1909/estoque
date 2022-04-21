package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutosRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String MENSAGEM_ERRO_REMOCAO = "Não foi possível remover o produto";
    private static final String MENSAGEM_ERRO_BUSCA_PRODUTOS = "Não foi possível carregar os produtos novos";
    private static final String MENSAGEM_ERRO_SALVA = "Não foi possível salvar o produto";
    private static final String MENSAGEM_ERRO_EDICAO = "Não foi possível editar o produto";
    private ListaProdutosAdapter adapter;
    private ProdutosRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        repository = new ProdutosRepository(this);
        repository.buscaProdutos(new ProdutosRepository.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> produtosNovos) {
                adapter.atualiza(produtosNovos);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(MENSAGEM_ERRO_BUSCA_PRODUTOS, Toast.LENGTH_LONG);
            }
        });
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener((posicao, produtoEscolhido) -> {
                    repository.remove(produtoEscolhido,
                            new ProdutosRepository.DadosCarregadosCallback<Void>() {
                                @Override
                                public void quandoSucesso(Void resultado) {
                                    adapter.remove(posicao);
                                }

                                @Override
                                public void quandoFalha(String erro) {
                                    mostraErro(MENSAGEM_ERRO_REMOCAO, Toast.LENGTH_LONG);
                                }
                            });
                }
        );
    }

    private void mostraErro(String mensagem, int lengthLong) {
        Toast.makeText(this,
                mensagem,
                lengthLong).show();
    }


    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto =
                findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, produtoCriado ->
                repository.salva(produtoCriado, new ProdutosRepository.DadosCarregadosCallback<Produto>() {
                    @Override
                    public void quandoSucesso(Produto produtoSalvo) {
                        adapter.adiciona(produtoSalvo);
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        mostraErro(MENSAGEM_ERRO_SALVA, Toast.LENGTH_LONG);
                    }
                })).mostra();
    }


    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoCriado -> repository.edita(produtoCriado, new ProdutosRepository.DadosCarregadosCallback<Produto>() {
                    @Override
                    public void quandoSucesso(Produto produtoEditado) {
                        adapter.edita(posicao, produtoEditado);
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        mostraErro(MENSAGEM_ERRO_EDICAO, Toast.LENGTH_SHORT);
                    }
                }))
                .mostra();
    }


}
