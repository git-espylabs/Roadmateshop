package com.roadmate.shop.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import com.roadmate.shop.R

class SearchSuggestionActivity : AppCompatActivity() {

    var searchText =""

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                /* Intent intent = new Intent(getApplicationContext(), SearchExpand.class);
                startActivity(intent);*/
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchText = newText
                return false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_suggestion)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val search = menu!!.findItem(R.id.action_search)
        val searchView =
            MenuItemCompat.getActionView(search) as SearchView
        searchView.isIconified = false
        search(searchView)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        this.finish()
    }
}