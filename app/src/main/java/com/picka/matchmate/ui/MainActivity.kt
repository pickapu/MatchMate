package com.picka.matchmate.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.picka.matchmate.databinding.ActivityMainBinding
import com.picka.matchmate.local.ProfileStatus
import com.picka.matchmate.repository.RefreshResult
import com.picka.matchmate.ui.adapter.MatchAdapter
import com.picka.matchmate.viewmodel.MatchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MatchAdapter

    private val viewModel: MatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        setupSwipeToRefresh()
    }

    private fun setupRecyclerView() {
        adapter = MatchAdapter(
            onAccept = { profile ->
                viewModel.setProfileStatus(profile.email, ProfileStatus.ACCEPTED)
            },
            onDecline = { profile ->
                viewModel.setProfileStatus(profile.email, ProfileStatus.DECLINED)
            }
        )
        binding.rvMatches.layoutManager = LinearLayoutManager(this)
        binding.rvMatches.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.allProfiles.observe(this) { profiles ->
            adapter.submitList(profiles)
        }

        viewModel.refreshState.observe(this) { state ->
            when (state) {
                is RefreshResult.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                    binding.tvError.visibility = android.view.View.GONE
                }
                is RefreshResult.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvError.visibility = android.view.View.GONE
                }
                is RefreshResult.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvError.visibility = android.view.View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchProfilesFromNetwork()
        }
    }
}
