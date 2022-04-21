package br.com.alura.estoque.retrofit.callback;

import static br.com.alura.estoque.retrofit.callback.MensagensCallback.FALHA_DE_COMUNICACAO;
import static br.com.alura.estoque.retrofit.callback.MensagensCallback.RESPOSTA_NAO_SUCEDIDA;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackSemRetorno implements Callback<Void> {

    private final RespostaCallBackSemRetorno callback;

    public CallbackSemRetorno(RespostaCallBackSemRetorno callback) {
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()){
            callback.quandoSucesso();
        }else {
            callback.quandoFalha(RESPOSTA_NAO_SUCEDIDA);
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Void> call, Throwable t) {
        callback.quandoFalha(FALHA_DE_COMUNICACAO + t.getMessage());
    }

    public interface RespostaCallBackSemRetorno{
        void quandoSucesso();
        void quandoFalha(String erro);
    }
}
