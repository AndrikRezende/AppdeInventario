package com.example.android.appdeinventario;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Andrik on 14/05/2018.
 */

public class DetalhesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {
                ProdutoContract.ProdutoEntry._ID,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE,
                ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL
        };
        return new CursorLoader(this,
                getIntent().getData(),
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null);                   // The sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        TextView nome = (TextView) findViewById(R.id.detalhes_nome);
        TextView quantidade = (TextView) findViewById(R.id.detalhes_quantidade);
        TextView preco = (TextView) findViewById(R.id.detalhes_preco);
        TextView telefone = (TextView) findViewById(R.id.detalhes_telefone);
        TextView email = (TextView) findViewById(R.id.detalhes_email);
        ImageView imagem = (ImageView) findViewById(R.id.detalhes_imagem);

        if(cursor.moveToFirst()) {
            int nomeColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME);
            int quantidadeColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE);
            int precoColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO);
            int telefoneColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE);
            int emailColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL);
            int imagemColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM);
            String currentNome = cursor.getString(nomeColunaIndice);
            int currentQuantidade = cursor.getInt(quantidadeColunaIndice);
            int currentPreco = cursor.getInt(precoColunaIndice);
            int currentTelefone = cursor.getInt(telefoneColunaIndice);
            String currentEmail = cursor.getString(emailColunaIndice);
            byte[] currentImagem=cursor.getBlob(imagemColunaIndice);
            nome.setText("Nome do produto:"+currentNome);
            quantidade.setText("Itens:"+currentQuantidade);
            preco.setText("Preço:"+currentPreco);
            if(currentTelefone>0) {
                telefone.setText("Telefone:" + currentTelefone);
            }
            else {
                Button botaoTelefone =(Button) findViewById(R.id.detalhes_botao_telefone);
                botaoTelefone.setVisibility(View.INVISIBLE);
            }
            if(currentEmail==null || currentEmail.equals("")) {
                Button botaoEmail =(Button) findViewById(R.id.detalhes_botao_email);
                botaoEmail.setVisibility(View.INVISIBLE);
            }
            else{
                email.setText("Email:" + currentEmail);
            }
            imagem.setImageBitmap(BitmapFactory.decodeByteArray(currentImagem,0,currentImagem.length));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void retirarQuantidade(View view){
        TextView textoQuantidade = (TextView) findViewById(R.id.detalhes_quantidade);
        int quantidade=Integer.parseInt(textoQuantidade.getText().toString().split(":")[1]);
        if(quantidade>0) {
            ContentValues values = new ContentValues();
            values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE, quantidade - 1);
            getContentResolver().update(getIntent().getData(), values, null, null);
        }
    }

    public void adicionarQuantidade(View view){
        TextView textoQuantidade = (TextView) findViewById(R.id.detalhes_quantidade);
        int quantidade=Integer.parseInt(textoQuantidade.getText().toString().split(":")[1]);
        ContentValues values = new ContentValues();
        values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE, quantidade + 1);
        getContentResolver().update(getIntent().getData(), values, null, null);
    }

    public void comprarPorTelefone(View view){
        TextView textoTelefone = (TextView) findViewById(R.id.detalhes_telefone);
        int telefone=Integer.parseInt(textoTelefone.getText().toString().split(":")[1]);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+telefone));
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(this,"Não foi possivel fazer a ligação para "+telefone,Toast.LENGTH_SHORT).show();
        }
    }

    public void comprarPorEmail(View view){
        TextView textoEmail = (TextView) findViewById(R.id.detalhes_email);
        String email=textoEmail.getText().toString().split(":")[1];
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+email));
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(this,"Não foi possivel fazer acessar o email" ,Toast.LENGTH_SHORT).show();
        }
    }

    public void deletarProduto(View view){
        showDialogDelete();
    }

    private void showDialogDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirma deletar este produto?");
        builder.setPositiveButton("Sim",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int rowsDeleted = getContentResolver().delete(getIntent().getData(),null,null);
                if(rowsDeleted>0) {
                    Toast.makeText(getApplicationContext(), "Produto deletado", Toast.LENGTH_SHORT).show();
                    NavUtils.navigateUpFromSameTask(DetalhesActivity.this);
                }
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
