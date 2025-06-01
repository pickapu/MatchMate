package com.picka.matchmate.ui.adapter

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.picka.matchmate.R
import com.picka.matchmate.databinding.ItemMatchCardBinding
import com.picka.matchmate.local.ProfileStatus
import com.picka.matchmate.local.UserProfile

class MatchAdapter(
    private val onAccept: (UserProfile) -> Unit,
    private val onDecline: (UserProfile) -> Unit
) : ListAdapter<UserProfile, MatchAdapter.MatchViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = ItemMatchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(view, onAccept, onDecline)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MatchViewHolder(
        private val binding: ItemMatchCardBinding,
        private val onAccept: (UserProfile) -> Unit,
        private val onDecline: (UserProfile) -> Unit
    ) : RecyclerView.ViewHolder(binding.cardContainer) {


        fun bind(profile: UserProfile) {
            Glide.with(binding.cardContainer)
                .load(profile.pictureUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        binding.ivProfileTV.background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(binding.cardContainer.context.getColor(R.color.teal_200))
                        }
                        binding.ivProfileTV.text = profile.fullName[0].toString().uppercase()
                        binding.ivProfile.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.ivProfileTV.visibility = View.GONE
                        if (resource != null) binding.ivProfile.setImageDrawable(resource)
                        return true
                    }

                })
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .submit()

            binding.tvName.text = profile.fullName
            binding.tvDetails.text = "Age: ${profile.age} | City: ${profile.city}"
            binding.tvEdRel.text = "${profile.education} | ${profile.religion}"
            binding.tvMatchScore.text = "Score: ${profile.matchScore}"

            binding.tvStatus.text = "Status: ${profile.status.name}"
            when (profile.status) {
                ProfileStatus.ACCEPTED -> {
                    binding.tvStatus.setTextColor(binding.cardContainer.context.getColor(android.R.color.holo_green_dark))
                }

                ProfileStatus.DECLINED -> {
                    binding.tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                }

                else -> {
                    binding.tvStatus.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                }
            }

            if (profile.status != ProfileStatus.PENDING) {
                binding.btnAccept.isEnabled = false
                binding.btnDecline.isEnabled = false
                binding.btnAccept.visibility = View.GONE
                binding.btnDecline.visibility = View.GONE

            } else {
                binding.btnAccept.visibility = View.VISIBLE
                binding.btnDecline.visibility = View.VISIBLE
                binding.btnAccept.isEnabled = true
                binding.btnDecline.isEnabled = true
            }

            binding.btnAccept.setOnClickListener {
                onAccept(profile)
            }
            binding.btnDecline.setOnClickListener {
                onDecline(profile)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem == newItem
        }
    }
}
