package com.bashirli.notebookkotlinsql

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display.Mode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.bashirli.notebookkotlinsql.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var arrayList: ArrayList<Model>
    private lateinit var adapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        var view=binding.root
        setContentView(view)
        arrayList=ArrayList<Model>()
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        adapter=Adapter(arrayList)
        binding.recyclerView.adapter=adapter
try {
    val database=this.openOrCreateDatabase("Note", MODE_PRIVATE,null)
    val cursor=database.rawQuery("SELECT * FROM Notes",null)
    val idIx=cursor.getColumnIndex("id")
    val nameIx=cursor.getColumnIndex("main")
    while (cursor.moveToNext()){
        val name=cursor.getString(nameIx)
        val id=cursor.getInt(idIx)
        val model=Model(id,name)
arrayList.add(model)
    }
    adapter.notifyDataSetChanged()
    cursor.close()
}catch (e:Exception){

}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater=MenuInflater(this)
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.addnote){
            var intent=Intent(this@MainActivity,NoteActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }



}