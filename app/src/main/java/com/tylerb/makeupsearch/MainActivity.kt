package com.tylerb.makeupsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tylerb.makeupsearch.model.Makeup
import com.tylerb.makeupsearch.retrofit.ApiCall
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_view.*
import kotlinx.android.synthetic.main.card_view.view.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var daggerApi: ApiCall

    lateinit var disposable: Disposable

    var list = ArrayList<Makeup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as App).makeupComponent.inject(this)

        val adapter = MakeupAdapter(list)

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String?): Boolean {
                loading.visibility = View.VISIBLE
                search?.let {
                    disposable = daggerApi.getMakeupBrand(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { makeup: List<Makeup> ->
                                loading.visibility = View.INVISIBLE
                                list.addAll(makeup)
                                adapter.notifyDataSetChanged()
                            },
                            { fail ->
                                println(fail.message)
                                loading.visibility = View.INVISIBLE
                            }
                        )
                }

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0?.length == 0){
                    list.clear()
                    adapter.notifyDataSetChanged()
                }
                return true
            }

        })

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter

    }

    inner class MakeupAdapter(private val list: ArrayList<Makeup>) :
        RecyclerView.Adapter<MakeupAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            Picasso.get().load(item.image_link).into(holder.image)
            holder.name.text = item.name
            holder.price.text = item.price
            holder.rating.text = item.rating
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image = view.makeup_image
            val name = view.makeup_name
            val price = view.makeup_price
            val rating = view.makeup_rating
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
