package com.ifs21049.lostandfound.presentation.lostfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21049.lostandfound.data.model.DelcomLostFound
import com.ifs21049.lostandfound.data.remote.MyResult
import com.ifs21049.lostandfound.data.remote.response.LostFoundResponse
import com.ifs21049.lostandfound.databinding.ActivityLostFoundDetailBinding
import com.ifs21049.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21049.lostandfound.presentation.ViewModelFactory
import java.io.File

class LostFoundDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostFoundDetailBinding
    private val viewModel by viewModels<LostFoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isChanged: Boolean = false

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostFoundManageActivity.RESULT_CODE) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostFoundDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }

    private fun setupAction() {
        val lostfoundId = intent.getIntExtra(KEY_LOST_FOUND_ID, 0)
        if (lostfoundId == 0) {
            finish()
            return
        }

        observeGetLostFound(lostfoundId)

        binding.appbarLostFoundDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, isChanged)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }

    private fun observeGetLostFound(lostfoundId: Int) {
        viewModel.getLostFound(lostfoundId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }

                is MyResult.Success -> {
                    showLoading(false)
                    loadLostFound(result.data.data.lostfound)
                }

                is MyResult.Error -> {
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }

    private fun loadLostFound(lostfound: LostFoundResponse) {
        showComponent(true)

        binding.apply {
            tvLostFoundDetailTitle.text = lostfound.title
            tvLostFoundDetailDate.text = "Dibuat pada: ${lostfound.createdAt}"
            tvLostFoundDetailDesc.text = lostfound.description
            tvLostFoundDetailStatus.text = lostfound.status

            cbLostFoundDetailIsFinished.isChecked = lostfound.isCompleted == 1

            cbLostFoundDetailIsFinished.setOnCheckedChangeListener { _, isChecked ->
                val status = if (lostfound.status is String && (lostfound.status == "lost" || lostfound.status == "found")) lostfound.status else null

                if (status != null) {
                    val isCompleted = if (isChecked) 1 else 0

                    viewModel.putLostFound(
                        lostfound.id,
                        lostfound.title,
                        lostfound.description,
                        isCompleted,
                        status
                    ).observeOnce { result ->
                        when (result) {
                            is MyResult.Error -> {
                                val action = if (isChecked) "Menandai" else "Batal menandai"
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "$action barang temuan: ${lostfound.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is MyResult.Success -> {
                                val action = if (isChecked) "Barang berhasil ditemukan" else "Berhasil batal menandai"
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "$action: ${lostfound.title}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if ((lostfound.isCompleted == 1) != isChecked) {
                                    isChanged = true
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }

            ivLostFoundDetailActionDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this@LostFoundDetailActivity)

                builder.setTitle("Konfirmasi Hapus Barang")
                    .setMessage("Anda yakin ingin menghapus barang temuan ini?")

                builder.setPositiveButton("Ya") { _, _ ->
                    observeDeleteLostFound(lostfound.id)
                }

                builder.setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }

            val status = if (lostfound.status is String && (lostfound.status == "lost" || lostfound.status == "found")) lostfound.status else null

            val coverFile: File = lostfound.cover as? File ?: File("") // Ganti "" dengan path file default atau kosong yang sesuai

            val isCompleted = if (lostfound.isCompleted == 1) 1 else null
            val delcomLostFound = DelcomLostFound(
                lostfound.id,
                lostfound.title,
                lostfound.description,
                isCompleted,
                coverFile,
                status
            )

            val intent = Intent(
                this@LostFoundDetailActivity,
                LostFoundManageActivity::class.java
            )
            intent.putExtra(LostFoundManageActivity.KEY_IS_ADD, false)
            intent.putExtra(LostFoundManageActivity.KEY_LOSTFOUND, delcomLostFound)
            launcher.launch(intent)
        }
    }

    private fun observeDeleteLostFound(lostfoundId: Int) {
        showComponent(false)
        showLoading(true)
        viewModel.deleteLostFound(lostfoundId).observeOnce { result ->
            result?.let {
                when (it) {
                    is MyResult.Error -> {
                        showComponent(true)
                        showLoading(false)
                        Toast.makeText(
                            this@LostFoundDetailActivity,
                            "Gagal menghapus barang temuan: ${it.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is MyResult.Success -> {
                        showLoading(false)

                        Toast.makeText(
                            this@LostFoundDetailActivity,
                            "Berhasil menghapus barang temuan",
                            Toast.LENGTH_SHORT
                        ).show()

                        val resultIntent = Intent()
                        resultIntent.putExtra(KEY_IS_CHANGED, true)
                        setResult(RESULT_CODE, resultIntent)
                        finishAfterTransition()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLostFoundDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showComponent(status: Boolean) {
        binding.llLostFoundDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }

    companion object {
        const val KEY_LOST_FOUND_ID = "data_lost_found_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
    }
}
