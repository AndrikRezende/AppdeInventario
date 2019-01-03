package com.example.android.appdeinventario;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNomeEditText;
    private EditText mQuantidadeEditText;
    private EditText mPrecoEditText;
    private EditText mTelefoneEditText;
    private EditText mEmailEditText;
    private Bitmap mBitmapImagem;
    private final int REQUEST_CODE=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        getLoaderManager().initLoader(0, null, this);

        mNomeEditText = (EditText)findViewById(R.id.edit_nome);
        mQuantidadeEditText = (EditText)findViewById(R.id.edit_quantidade);
        mPrecoEditText = (EditText)findViewById(R.id.edit_preco);
        mTelefoneEditText = (EditText)findViewById(R.id.edit_telefone);
        mEmailEditText = (EditText)findViewById(R.id.edit_email);

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
                ProdutoContract.ProdutoEntry.CONTENT_URI,
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null);                   // The sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor!=null && cursor.moveToLast()){
            int nomeColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME);
            int quantidadeColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE);
            int precoColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO);
            int telefoneColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE);
            int emailColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL);
            int imagemColunaIndice=cursor.getColumnIndex(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM);
            String currentNome = cursor.getString(nomeColunaIndice);
            String currentQuantidade = cursor.getString(quantidadeColunaIndice);
            String currentPreco = cursor.getString(precoColunaIndice);
            String currentTelefone = cursor.getString(telefoneColunaIndice);
            String currentEmail = cursor.getString(emailColunaIndice);
            byte[] currentImagem=cursor.getBlob(imagemColunaIndice);
            mNomeEditText.setText(currentNome);
            mQuantidadeEditText.setText(currentQuantidade);
            mPrecoEditText.setText(currentPreco);
            mTelefoneEditText.setText(currentTelefone);
            mEmailEditText.setText(currentEmail);
            mBitmapImagem=BitmapFactory.decodeByteArray(currentImagem,0,currentImagem.length);
            ImageView imageView = (ImageView) findViewById(R.id.edit_imagem);
            imageView.setImageBitmap(mBitmapImagem);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void buscarImagem(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE & resultCode==RESULT_OK) {
            Uri uriImagem = data.getData();
            try {
                mBitmapImagem = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriImagem));
            }
            catch (Exception e){

            }
            ImageView imageView = (ImageView) findViewById(R.id.edit_imagem);
            imageView.setImageBitmap(mBitmapImagem);
        }
    }

    public void salvarDados(View view){
        String nome="";
        Integer quantidade=-1;
        Integer preco=-1;
        Integer telefone=-1;
        String email="";
        byte[]imagem=null;

        try{
            nome = mNomeEditText.getText().toString().trim();
            quantidade = Integer.parseInt(mQuantidadeEditText.getText().toString().trim());
            preco = Integer.parseInt(mPrecoEditText.getText().toString().trim());
            email = mEmailEditText.getText().toString().trim();
            telefone = Integer.parseInt(mTelefoneEditText.getText().toString().trim());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mBitmapImagem.compress(Bitmap.CompressFormat.JPEG,0,outputStream);
            imagem = outputStream.toByteArray();
            outputStream.close();
        }
        catch (Exception e){
            Toast.makeText(this,"Erro: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        ContentValues values = new ContentValues();
        values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME,nome);
        values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE,quantidade);
        values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO,preco);
        values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM,imagem);
        values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE,telefone);
        values.put(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL,email);

        Uri newUri = getContentResolver().insert(ProdutoContract.ProdutoEntry.CONTENT_URI,values);
        if (newUri == null) {
            Toast.makeText(this, "Erro em salvar dados", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dados salvo com id " + newUri, Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);
        }
    }

}
