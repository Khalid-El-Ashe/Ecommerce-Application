package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.Product
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // this to don't make instance for this class when you use it
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals: StateFlow<Resource<List<Product>>> = _bestDeals

    private val _bestProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct: StateFlow<Resource<List<Product>>> = _bestProduct

    // i need to make instance for paging class
    private val pagingInfo = PagingInfo()

    // make inicialize the fetch function
    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    // this function to fetch the data from fireStore
    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        // in here i need to make filter with Query to get the data same this
        firestore.collection("Products").whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener { result ->
                val specialProductsList =
                    result.toObjects(Product::class.java) // convert the result into this value
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDeals.emit(Resource.Loading())
        }

        // in here i need to make filter with Query to get the data same this
        firestore.collection("Products").whereEqualTo("category", "Best Deals").get()
            .addOnSuccessListener { result ->
                val bestDealsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Success(bestDealsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProduct.emit(Resource.Loading())
            }

            firestore.collection("Products")
//                .whereEqualTo("category", "chair")
//                .orderBy("id", Query.Direction.ASCENDING) // orderBy : to filter حتى تقوم بالترتيب تنازلي او تصاعدي حسب ما نريد
                .limit(pagingInfo.bestProductsPage * 10) // pagingInfo.paging * 10 : that men is page number 1 i need to get 10 products
                .get()
                .addOnSuccessListener { result ->
                    val bestProductsList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd =
                        bestProductsList == pagingInfo.oldBestProducts // this for paging to update the list
                    pagingInfo.oldBestProducts = bestProductsList
                    viewModelScope.launch {
                        _bestProduct.emit(Resource.Success(bestProductsList))
                    }
                    pagingInfo.bestProductsPage++ // this is to get more page 1, 2, 3,......
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProduct.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}

// i need to make class for paging3
internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)