package br.edu.scl.ifsp.sdm.contactlist.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.scl.ifsp.sdm.contactlist.R
import br.edu.scl.ifsp.sdm.contactlist.adapter.ContactAdapter
import br.edu.scl.ifsp.sdm.contactlist.databinding.ActivityMainBinding
import br.edu.scl.ifsp.sdm.contactlist.model.Constant.EXTRA_CONTACT
import br.edu.scl.ifsp.sdm.contactlist.model.Constant.EXTRA_VIEW_CONTACT
import br.edu.scl.ifsp.sdm.contactlist.model.Contact

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Data Source
    private val contactList: MutableList<Contact> = mutableListOf()

    // Adapter
    private val contactAdapter: ContactAdapter by lazy {
        ContactAdapter(this, contactList)
    }

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = getString(R.string.contact_list)

        carl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
          if (result.resultCode == RESULT_OK) {
              val contact = result.data?.getParcelableExtra<Contact>(EXTRA_CONTACT)
              contact?.also { contactResult ->
                  if (contactList.any{ it.id == contactResult.id }) {
                      // Editar na lista
                      val position = contactList.indexOfFirst { it.id == contactResult.id }
                      contactList[position] = contactResult
                  } else {
                      // Adicionar na lista
                      contactList.add(contactResult)
                  }
                  contactAdapter.notifyDataSetChanged()
              }
          }
        }

        fillContacts()

        amb.contactLv.adapter = contactAdapter

        registerForContextMenu(amb.contactLv)

        amb.contactLv.setOnItemClickListener { _, _, position, _ ->
            val contact = contactList[position]
            startActivity(Intent(this, ContactActivity::class.java).apply {
                putExtra(EXTRA_CONTACT, contact)
                putExtra(EXTRA_VIEW_CONTACT, true)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addContactMi -> {
                carl.launch(Intent(this, ContactActivity::class.java))
                true
            }
            else -> { false }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterContextMenuInfo).position
        return when(item.itemId) {
            R.id.removeContactMi -> {
                contactList.removeAt(position)
                contactAdapter.notifyDataSetChanged()
                Toast.makeText(this, getString(R.string.contact_removed), Toast.LENGTH_SHORT).show()
                true
            }

            R.id.editContactMi -> {
                val contact = contactList[position]
                carl.launch(Intent(this, ContactActivity::class.java).apply {
                    putExtra(EXTRA_CONTACT, contact)
                })
                true
            }

            else -> { false }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterForContextMenu(amb.contactLv)
    }

    private fun fillContacts() {
        for (i in 1..10) {
            contactList.add(
                Contact(
                    i,
                    "Nome $i",
                    "Adress $i",
                    "Telefone $i",
                    "Email $i"
                )
            )
        }
    }
}