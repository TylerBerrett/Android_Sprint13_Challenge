package com.tylerb.makeupsearch

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.picasso.Picasso
import com.tylerb.makeupsearch.model.Makeup
import com.tylerb.makeupsearch.retrofit.ApiCall
import com.tylerb.makeupsearch.util.breadCrumb
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_view.*
import kotlinx.android.synthetic.main.card_view.view.*
import java.lang.NullPointerException
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

        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, this.localClassName, "test")

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
                                if (!makeup.isNullOrEmpty()){
                                    list.addAll(makeup)
                                    adapter.notifyDataSetChanged()
                                } else Toast.makeText(this@MainActivity,
                                    "Please enter a valid brand", Toast.LENGTH_SHORT).show()
                            },
                            { fail -> loading.visibility = View.INVISIBLE }
                        )
                }

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, search)
                FirebaseAnalytics.getInstance(this@MainActivity).logEvent("user_search", bundle)

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

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            Picasso.get().load(item.image_link).into(holder.image)
            holder.name.text = item.name
            holder.price.text = "$${item.price}"
            if (!item.rating.isNullOrBlank()) holder.rating.text = "${item.rating}/5.0"
            else holder.rating.text = "This item is trash, it has no rating"

            holder.cardView.setOnLongClickListener {
                // used for Crashlytics
                breadCrumb(this@MainActivity.localClassName, item.name)
                throw (NullPointerException())
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardView: CardView = view.card_view
            val image: ImageView = view.makeup_image
            val name: TextView = view.makeup_name
            val price: TextView = view.makeup_price
            val rating: TextView = view.makeup_rating
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
