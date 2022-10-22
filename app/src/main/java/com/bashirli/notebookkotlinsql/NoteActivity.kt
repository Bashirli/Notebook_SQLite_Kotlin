package com.bashirli.notebookkotlinsql

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bashirli.notebookkotlinsql.databinding.ActivityMainBinding
import com.bashirli.notebookkotlinsql.databinding.ActivityNoteBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest
import kotlin.contracts.contract

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    lateinit var permissionLauncher:ActivityResultLauncher<String>
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var uriImage:Uri
     var bitmapImage:Bitmap?=null
    lateinit var database:SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNoteBinding.inflate(layoutInflater)
        var view=binding.root
        setContentView(view)
        activity_launcher()

        database=this.openOrCreateDatabase("Note", MODE_PRIVATE,null)
        database.execSQL("CREATE TABLE IF NOT EXISTS Notes (id INTEGER PRIMARY KEY,main NVARCHAR,qeyd NVARCHAR,image BLOB)")


        val info=intent.getStringExtra("info")
        if(info.equals("old")){
            binding.button.visibility=View.GONE
            binding.button2.visibility=View.VISIBLE
            val cursor=database.rawQuery("SELECT * FROM Notes WHERE id=?", arrayOf(intent.getStringExtra("id")))
            val mainIx = cursor.getColumnIndex("main")
            val qeydIx = cursor.getColumnIndex("qeyd")

            val imageIx = cursor.getColumnIndex("image")
            while (cursor.moveToNext()) {
                binding.editTextTextPersonName.setText(cursor.getString(mainIx))
                binding.editTextTextMultiLine.setText(cursor.getString(qeydIx))
                if (cursor.getBlob(imageIx) != null) {
                    binding.imageView.setImageBitmap(
                        BitmapFactory.decodeByteArray(
                            cursor.getBlob(
                                imageIx
                            ), 0, cursor.getBlob(imageIx).size
                        )
                    )
                }
            }
        }else{
            binding.button2.visibility=View.GONE
            binding.button.visibility=View.VISIBLE
            binding.editTextTextMultiLine.setText("")

            binding.editTextTextPersonName.setText("")
        binding.imageView.setImageResource(R.drawable.ic_launcher_background)
        }

   }

    fun activity_launcher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK){
            var intent=it.data
            if(intent!=null){
                uriImage= intent.data!!
                try {
                    if(Build.VERSION.SDK_INT>=28){
                        var source:ImageDecoder.Source=ImageDecoder.createSource(this@NoteActivity.contentResolver,uriImage)
                        bitmapImage=ImageDecoder.decodeBitmap(source)
                        binding.imageView.setImageBitmap(bitmapImage)

                    }else{
                        bitmapImage=MediaStore.Images.Media.getBitmap(contentResolver,uriImage)
                        binding.imageView.setImageBitmap(bitmapImage)

                    }
                }catch (e:Exception){
                    e.localizedMessage
                }


            }
        }
        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){

           if(it){
               var intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intent)
           }else{
            Toast.makeText(this@NoteActivity,"Icaze verilmedi.",Toast.LENGTH_LONG).show()
           }
        }


    }

    fun selectImage(view:View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Icazə yoxdur!",Snackbar.LENGTH_INDEFINITE).setAction("Icazə ver"){
                    //permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

                }.show()
            }else{
                //permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

            }
        }else{
            //activity

            var intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }
    }


    fun sil(view:View){
    database.execSQL("DELETE  FROM Notes  where id=?", arrayOf(intent.getStringExtra("id")))

        var intent =Intent(this@NoteActivity,MainActivity::class.java)
        startActivity(intent)
        finish()

    }


    fun problemTapma():Int{
        if(binding.editTextTextPersonName.text.toString().equals("")||
                binding.editTextTextMultiLine.text.toString().equals("")){
            Toast.makeText(this@NoteActivity,"Xana boş buraxılıb!",Toast.LENGTH_LONG).show()
        return 0
        }

        return 1
    }



    fun elaveEt(view:View){
        if(problemTapma()==0){
            return
        }

        if(bitmapImage!=null){

            try {
                var byteArrayOutputStream=ByteArrayOutputStream()
                bitmapImage!!.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
                var byteArray=byteArrayOutputStream.toByteArray()
                var sqlString="INSERT INTO Notes (main,qeyd,image) VALUES (?,?,?)"
                var statement=database.compileStatement(sqlString)
                statement.bindString(1,binding.editTextTextPersonName.text.toString())
                statement.bindString(2,binding.editTextTextMultiLine.text.toString())
                statement.bindBlob(3,byteArray)
                statement.execute()


            }catch (e:Exception){
                println(e.localizedMessage)
            }

        }else {
            var sqlString="INSERT INTO Notes (main,qeyd,image) VALUES (?,?,?)"
            var statement=database.compileStatement(sqlString)
            statement.bindString(1,binding.editTextTextPersonName.text.toString())
            statement.bindString(2,binding.editTextTextMultiLine.text.toString())
            statement.bindNull(3)
            statement.execute()
        }


        var intent =Intent(this@NoteActivity,MainActivity::class.java)
        startActivity(intent)
        finish()


    }



}