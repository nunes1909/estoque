package br.com.alura.estoque.retrofit.callback;

import static br.com.alura.estoque.retrofit.callback.MensagensCallback.FALHA_DE_COMUNICACAO;
import static br.com.alura.estoque.retrofit.callback.MensagensCallback.RESPOSTA_NAO_SUCEDIDA;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallbackComRetorno<T> implements Callback<T> {

    private final RespostaCallback<T> callback;

    public CallbackComRetorno(RespostaCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()){
            T resultado = response.body();
            if (resultado != null){
                //notifica que tem resposta
                callback.quandoSucesso(resultado);
            }
        }else {
            //notifica falha
            callback.quandoFalha(RESPOSTA_NAO_SUCEDIDA);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        //notifica falha
        callback.quandoFalha(FALHA_DE_COMUNICACAO + t.getMessage());
    }


    public interface RespostaCallback<T> {
        void quandoSucesso(T resultado);
        void quandoFalha(String erro);
    }
}
