package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.devbyteviewer.repository.AsteroidsRepository
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.databinding.GridViewItemBinding
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel= viewModel

        binding.asteroidRecycler.adapter=AsteroidAdapter(AsteroidAdapter.OnClickListener{
            viewModel.displayPropertyDetails(it)
        })
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayPropertyDetailsComplete()
            }
        })
        viewModel.asteroidsList.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                val adapter = asteroid_recycler.adapter as AsteroidAdapter
                adapter.submitList(it)
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


   @RequiresApi(Build.VERSION_CODES.O)
   override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId){
                R.id.show_today_menu -> viewModel.updateFilter(MainViewModel.MenuItemFilter.SHOW_TODAY)
                R.id.show_week_menu -> viewModel.updateFilter(MainViewModel.MenuItemFilter.WEEK)
                R.id.show_saved_menu -> viewModel.updateFilter(MainViewModel.MenuItemFilter.SAVED)
            }
       return true
    }
}
