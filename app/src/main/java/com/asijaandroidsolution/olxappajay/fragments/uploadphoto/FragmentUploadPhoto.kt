package com.asijaandroidsolution.olxappajay.fragments.uploadphoto

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.activity.main.MainActivity
import com.asijaandroidsolution.olxappajay.activity.preview.PreviewImage
import com.asijaandroidsolution.olxappajay.databinding.FragmentSellBinding
import com.asijaandroidsolution.olxappajay.databinding.FragmentUploadPhotoBinding
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.fragments.uploadphoto.adapter.UploadPhotoAdapter
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.OnImageSelectedListener
import com.asijaandroidsolution.olxappajay.utils.SharedPref
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import net.alhazmy13.mediapicker.Image.ImagePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentUploadPhoto : BaseFragment(), View.OnClickListener, UploadPhotoAdapter.AddImage {
    private val imageUriList=ArrayList<String>()
    private var count=0
    private lateinit var uploadTask: UploadTask
    private var imagesAdapter: UploadPhotoAdapter? = null
    private val selectedImagesArrayList= ArrayList<String>()
    private var outputFileUri: String? = null
    private lateinit var binding: FragmentUploadPhotoBinding
    private var dialog: BottomSheetDialog?=null
    internal var selectedImage:File?=null
    internal var TAG=FragmentUploadPhoto::class.java.simpleName
    val db=FirebaseFirestore.getInstance()
    internal lateinit var storageRef:StorageReference
    internal lateinit var imageRef:StorageReference
    internal lateinit var storage:FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadPhotoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvImages.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        storage= FirebaseStorage.getInstance()
        storageRef=storage.getReference()
        getSelectedImage()
        listener()
        registerCallback()
    }

    private fun registerCallback() {
        (activity as MainActivity).getOnImageSelected(object :OnImageSelectedListener{
            override fun onImageSelected(bundle: Bundle) {
                binding.linearLayoutChoosePhoto.visibility=View.GONE
                binding.rvImages.visibility=View.VISIBLE
                val mPaths= bundle.getStringArrayList(Constants.IMAGE_PATH)
                selectedImage= File(mPaths!![0])
                outputFileUri=mPaths[0]
                selectedImagesArrayList.add(mPaths[0])
                setAdapter()
            }

        })
    }

    private fun setAdapter() {
        if (imagesAdapter!=null){
            imagesAdapter!!.customNotify(selectedImagesArrayList)
        }else{
            imagesAdapter=UploadPhotoAdapter(requireActivity(),selectedImagesArrayList,this)
            binding.rvImages.adapter=imagesAdapter
        }
    }

    private fun listener() {
        binding.ivUploadPhoto.setOnClickListener(this)
        binding.btnPreview.setOnClickListener(this)
        binding.btnUpload.setOnClickListener(this)
    }

    private fun getSelectedImage() {

    }

    override fun onClick(v: View?) {
        when(v!!.getId()){
            R.id.iv_upload_photo->{
                if(selectedImagesArrayList.size<3)
                    showBottomSheetDialog()
                else
                    Toast.makeText(requireActivity(),"You Can not add more photos",Toast.LENGTH_SHORT).show()
            }
            R.id.btnPreview->{
                startActivity(Intent(activity, PreviewImage::class.java).putExtra("imageuri",outputFileUri))
            }

            R.id.btnUpload ->{

                if (selectedImage==null||!selectedImage!!.exists()){

                    Toast.makeText(activity,"Please Select Photo",Toast.LENGTH_SHORT).show()
                }
                else{
                    saveFileInFireStoreDb()
                }
            }
        }
    }

    private fun saveFileInFireStoreDb() {
        showProgressbar()
        for (i in 0..selectedImagesArrayList.size-1){

            val file=File(selectedImagesArrayList[i])
            uploadImage(file,file.name,i)
        }
    }

    private fun uploadImage(file: File, name: String, i: Int) {
        imageRef=storageRef.child("images/$name")
        uploadTask=imageRef.putFile(Uri.fromFile(file))

        uploadTask.addOnSuccessListener(object :OnSuccessListener<UploadTask.TaskSnapshot>{
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                imageRef.downloadUrl.addOnSuccessListener {
                    count++
                    val url=it.toString()
                    imageUriList.add(url)
                    if (count==selectedImagesArrayList.size){
                        postAd()
                    }
                }

            }

        })


    }

    private fun postAd() {
        val date=curentDate()

        var docId=db.collection(arguments?.getString(Constants.CATEGORIE_NAME)!!).document().id
        val docData= hashMapOf(
            Constants.ADDRESS to arguments?.getString(Constants.ADDRESS),
            Constants.BRAND to arguments?.getString(Constants.BRAND),
            Constants.DESCRIPTION to arguments?.getString(Constants.DESCRIPTION),
            Constants.YEAR to arguments?.getString(Constants.YEAR),
            Constants.PRICE to arguments?.getString(Constants.PRICE),
            Constants.ADDRESS to arguments?.getString(Constants.ADDRESS),
            Constants.PHONE to arguments?.getString(Constants.PHONE),
            Constants.Id to docId,
            Constants.TYPE to arguments?.getString(Constants.CATEGORIE_NAME),
            Constants.USER_ID to SharedPref(requireActivity()).getString(Constants.USER_ID),
            Constants.CREATED_Date to date,
            "images" to imageUriList
        )
        val category=arguments?.getString(Constants.CATEGORIE_NAME)
        db.collection(arguments?.getString(Constants.CATEGORIE_NAME)!!)

            .add(docData)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                Log.w(TAG, "DataasId" + it.id)

                updateDocId(it.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }

    }

    private fun curentDate(): String {
        val date=Date()
        val format=SimpleDateFormat("dd MMM, yyyy")
        return format.format(date)
    }

    private fun updateDocId(id: String) {
        val docData = hashMapOf(
            Constants.Id to id
        )
        db.collection(arguments?.getString(Constants.CATEGORIE_NAME)!!)
            .document(id.toString())
            .update(docData as Map<String, Any>).addOnSuccessListener {
                hideProgressbar()
                Toast.makeText(activity, "Ad Posted Successfully", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_upload_photo_to_my_ads)
            }

    }

    private fun showBottomSheetDialog() {
        val layoutInflater=requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= layoutInflater.inflate(R.layout.bottom_sheet_dialog,null)
        dialog= BottomSheetDialog(requireActivity())
        dialog!!.setContentView(view)
        dialog!!.window!!.findViewById<View>(R.id.design_bottom_sheet)
            .setBackgroundColor(resources.getColor(android.R.color.transparent))

        val tvGallery=dialog!!.findViewById<TextView>(R.id.tvGallery)
        val tvCamera=dialog!!.findViewById<TextView>(R.id.tvCamera)
        val tvCancel=dialog!!.findViewById<TextView>(R.id.tvCancel)

        tvCamera!!.setOnClickListener(View.OnClickListener {
            dialog!!.dismiss()
            chooseImage(ImagePicker.Mode.CAMERA)
        })
        tvGallery!!.setOnClickListener(View.OnClickListener {
            dialog!!.dismiss()
            chooseImage(ImagePicker.Mode.GALLERY)
        })

        tvCancel!!.setOnClickListener(View.OnClickListener {
            dialog!!.dismiss()
        })
        dialog!!.show()
        val winParams=WindowManager.LayoutParams()
        val window= dialog!!.window
        winParams.copyFrom(window!!.attributes)
        winParams.width=WindowManager.LayoutParams.MATCH_PARENT
        winParams.height=WindowManager.LayoutParams.MATCH_PARENT
        window!!.attributes=winParams
    }

    private fun chooseImage(mode:ImagePicker.Mode){
        ImagePicker.Builder(requireActivity())
            .mode(mode)
            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
            .directory(ImagePicker.Directory.DEFAULT)
            .extension(ImagePicker.Extension.PNG)
            .allowMultipleImages(false)
            .enableDebuggingMode(true)
            .build();
    }

    override fun addImage() {
        if (selectedImagesArrayList.size<3){
            showBottomSheetDialog()
        }
        else{
            Toast.makeText(activity,"You can not add more images",Toast.LENGTH_SHORT).show()
        }
    }

}