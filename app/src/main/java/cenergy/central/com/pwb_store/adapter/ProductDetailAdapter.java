package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductDeliveryViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductDetailDescriptionViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductDetailImageViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductOverviewViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductPromotionViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductSpecViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SearchResultViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetailAvailableOptionItem;
import cenergy.central.com.pwb_store.model.ProductDetailImage;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;
import cenergy.central.com.pwb_store.model.ReviewDetailText;
import cenergy.central.com.pwb_store.model.SpecDao;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ProductDetailAdapter.class.getSimpleName();

    //Static members
    private static final int VIEW_TYPE_ID_IMAGE = 0;
    private static final int VIEW_TYPE_ID_DETAIL_ITEM = 1;
    private static final int VIEW_TYPE_ID_DETAIL_OVERVIEW = 2;
    private static final int VIEW_TYPE_ID_DETAIL_PROMOTION = 3;
    private static final int VIEW_TYPE_ID_DETAIL_SPEC = 4;
    private static final int VIEW_TYPE_ID_DETAIL_DELIVERY = 5;
    private static final int VIEW_TYPE_ID_RESULT = 6;
    //private static final int VIEW_TYPE_ID_RECOMMEND_ITEM = 6;

    private static final ViewType VIEW_TYPE_OVERVIEW = new ViewType(VIEW_TYPE_ID_DETAIL_OVERVIEW);
    private static final ViewType VIEW_TYPE_PROMOTION = new ViewType(VIEW_TYPE_ID_DETAIL_PROMOTION);
    private static final ViewType VIEW_TYPE_SPEC = new ViewType(VIEW_TYPE_ID_DETAIL_SPEC);
    private static final ViewType VIEW_TYPE_DELIVERY = new ViewType(VIEW_TYPE_ID_DETAIL_DELIVERY);
    private static final ViewType VIEW_TYPE_RESULT = new ViewType(VIEW_TYPE_ID_RESULT);

    //Data Members
    private Context mContext;
    private FragmentManager mFragmentManager;
    private ProductDetail mProductDetail;
    private Product mProduct;
    private boolean isLoadingShow = false;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_IMAGE:
                    return 3;
                case VIEW_TYPE_ID_DETAIL_ITEM:
                    return 4;
                case VIEW_TYPE_ID_DETAIL_OVERVIEW:
                    return 7;
                case VIEW_TYPE_ID_DETAIL_PROMOTION:
                    return 7;
                case VIEW_TYPE_ID_DETAIL_SPEC:
                    return 7;
                case VIEW_TYPE_ID_DETAIL_DELIVERY:
                    return 7;
//                case VIEW_TYPE_ID_RECOMMEND_ITEM:
//                    return 7;
                case VIEW_TYPE_ID_RESULT:
                    return 7;
                default:
                    return 1;
            }
        }
    };

    public ProductDetailAdapter(Context context, FragmentManager mFragmentManager) {
        this.mContext = context;
        this.mFragmentManager = mFragmentManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_IMAGE:
                return new ProductDetailImageViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_detail_image_main, parent, false)
                );
            case VIEW_TYPE_ID_DETAIL_ITEM:
                return new ProductDetailDescriptionViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_detail_description, parent, false)
                );
            case VIEW_TYPE_ID_DETAIL_OVERVIEW:
                return new ProductOverviewViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_overview, parent, false)
                );
            case VIEW_TYPE_ID_DETAIL_SPEC:
                return new ProductSpecViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_detail_spec, parent, false)
                );
            case VIEW_TYPE_ID_DETAIL_PROMOTION:
                return new ProductPromotionViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_detail_promotion, parent, false)
                );

            case VIEW_TYPE_ID_DETAIL_DELIVERY:
                return new ProductDeliveryViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_detail_delivery, parent, false)
                );

            case VIEW_TYPE_ID_RESULT:
                return new SearchResultViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_loading_result, parent, false)
                );

//            case VIEW_TYPE_ID_RECOMMEND_ITEM:
//                return new ProductRecommendListViewHolder(
//                        LayoutInflater
//                                .from(parent.getContext())
//                                .inflate(R.layout.list_item_recommend, parent, false)
//                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_IMAGE:
                if (viewType instanceof ProductDetailImage && holder instanceof ProductDetailImageViewHolder) {
                    ProductDetailImage productDetailImage = (ProductDetailImage) viewType;
                    ProductDetailImageViewHolder productDetailImageViewHolder = (ProductDetailImageViewHolder) holder;
                    productDetailImageViewHolder.setViewHolder(productDetailImage, mFragmentManager);
                }

                break;

            case VIEW_TYPE_ID_DETAIL_ITEM:
                if (viewType instanceof ProductDetail && holder instanceof ProductDetailDescriptionViewHolder) {
                    ProductDetail productDetail = (ProductDetail) viewType;
                    ProductDetailDescriptionViewHolder productDetailDescriptionViewHolder = (ProductDetailDescriptionViewHolder) holder;
                    productDetailDescriptionViewHolder.setViewHolder(productDetail);
                }
                if (viewType instanceof Product && holder instanceof ProductDetailDescriptionViewHolder) {
                    Product product = (Product) viewType;
                    ProductDetailDescriptionViewHolder productDetailDescriptionViewHolder = (ProductDetailDescriptionViewHolder) holder;
                    productDetailDescriptionViewHolder.setViewHolder(product);
                }
                break;

            case VIEW_TYPE_ID_DETAIL_OVERVIEW:
                if (viewType instanceof ReviewDetailText && holder instanceof ProductOverviewViewHolder) {
                    ReviewDetailText reviewDetailText = (ReviewDetailText) viewType;
                    ProductOverviewViewHolder productOverviewViewHolder = (ProductOverviewViewHolder) holder;
                    productOverviewViewHolder.setViewHolder(reviewDetailText);
                }
                break;

            case VIEW_TYPE_ID_DETAIL_SPEC:
                if (viewType instanceof SpecDao && holder instanceof ProductSpecViewHolder) {
                    SpecDao specDao = (SpecDao) viewType;
                    ProductSpecViewHolder productSpecViewHolder = (ProductSpecViewHolder) holder;
                    productSpecViewHolder.setViewHolder(specDao);
                }
                break;

            case VIEW_TYPE_ID_DETAIL_PROMOTION:
                if (holder instanceof ProductPromotionViewHolder) {
                    ProductPromotionViewHolder productPromotionViewHolder = (ProductPromotionViewHolder) holder;
                    productPromotionViewHolder.setViewHolder(mFragmentManager, mProductDetail);
                }
                break;

            case VIEW_TYPE_ID_DETAIL_DELIVERY:
                if (holder instanceof ProductDeliveryViewHolder) {
                    ProductDeliveryViewHolder productDeliveryViewHolder = (ProductDeliveryViewHolder) holder;
                    productDeliveryViewHolder.setViewHolder(mContext, mProductDetail);
                }
                break;

//            case VIEW_TYPE_ID_RECOMMEND_ITEM:
//                if (viewType instanceof Recommend && holder instanceof ProductRecommendListViewHolder) {
//                    Recommend recommend = (Recommend) viewType;
//                    ProductRecommendListViewHolder productRecommendListViewHolder = (ProductRecommendListViewHolder) holder;
//                    productRecommendListViewHolder.setViewHolder(mFragmentManager, recommend);
//                }
//                break;
        }
    }

    @Override
    public int getItemCount() {
        return mListViewType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListViewType.get(position).getViewTypeId();
    }

    public void setProductDetail(ProductDetail productDetail) {
        this.mProductDetail = productDetail;
//        if (productDetail.getProductImageList() != null) {
//            ProductDetailImage productDetailImage = productDetail.getProductImageList();
//            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
//            mListViewType.add(productDetailImage);
//        }
        //if (productDetail.getProductDetailImageItems() != null || productDetail.getProductImageList() != null) {
        if (productDetail.getProductDetailImageItems() == null) {
            ProductDetailImage productDetailImage = productDetail.getExtensionProductDetail().getProductImageList();
//            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
//            mListViewType.add(productDetailImage);

            int i;
            for (i = 0; i < productDetailImage.getProductDetailImageItems().size(); i++) {
                Log.d(TAG, "total : " + i);
            }
            ProductDetailImage productDetailImages = new ProductDetailImage(i, productDetailImage.getProductDetailImageItems());
            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
            mListViewType.add(productDetailImages);

        } else {
            int i;
            for (i = 0; i < productDetail.getProductDetailImageItems().size(); i++) {
                Log.d(TAG, "total : " + i);
            }
            ProductDetailImage productDetailImage = new ProductDetailImage(i, productDetail.getProductDetailImageItems());
            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
            mListViewType.add(productDetailImage);
        }

        productDetail.setViewTypeId(VIEW_TYPE_ID_DETAIL_ITEM);
        mListViewType.add(productDetail);

        if (productDetail.getExtensionProductDetail().getDescription() != null || productDetail.getReviewEN() != null) {
            ReviewDetailText reviewDetailText = new ReviewDetailText(productDetail.getExtensionProductDetail().getDescription(), ReviewDetailText.MODE_DESCRIPTION);
            reviewDetailText.setViewTypeId(VIEW_TYPE_ID_DETAIL_OVERVIEW);
            mListViewType.add(reviewDetailText);
        } else {
            mListViewType.add(VIEW_TYPE_OVERVIEW);
        }

        SpecDao specDao = productDetail.getSpecDao();
        specDao.setViewTypeId(VIEW_TYPE_ID_DETAIL_SPEC);
        mListViewType.add(specDao);

        mListViewType.add(VIEW_TYPE_PROMOTION);
        mListViewType.add(VIEW_TYPE_DELIVERY);

        notifyDataSetChanged();

    }

    public void setProductDetail(Product product) {
        this.mProduct = product;
//        if (productDetail.getProductImageList() != null) {
            ProductDetailImage productDetailImage = product.getProductImageList();
            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
            mListViewType.add(productDetailImage);
//        }
        //if (productDetail.getProductDetailImageItems() != null || productDetail.getProductImageList() != null) {
//        if (productDetailNew.getProductDetailImageItems() == null) {
//            ProductDetailImage productDetailImage = productDetailNew.getExtensionProductDetail().getProductImageList();
////            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
////            mListViewType.add(productDetailImage);
//
//            int i;
//            for (i = 0; i < productDetailImage.getProductDetailImageItems().size(); i++) {
//                Log.d(TAG, "total : " + i);
//            }
//            ProductDetailImage productDetailImages = new ProductDetailImage(i, productDetailImage.getProductDetailImageItems());
//            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
//            mListViewType.add(productDetailImages);
//
//        } else {
//            int i;
//            for (i = 0; i < productDetailNew.getProductDetailImageItems().size(); i++) {
//                Log.d(TAG, "total : " + i);
//            }
//            ProductDetailImage productDetailImage = new ProductDetailImage(i, productDetailNew.getProductDetailImageItems());
//            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
//            mListViewType.add(productDetailImage);
//        }

        product.setViewTypeId(VIEW_TYPE_ID_DETAIL_ITEM);
        mListViewType.add(product);

//        if (productDetailNew.getExtensionProductDetail().getDescription() != null || productDetailNew.getReviewEN() != null) {
//            ReviewDetailText reviewDetailText = new ReviewDetailText(productDetailNew.getExtensionProductDetail().getDescription(), ReviewDetailText.MODE_DESCRIPTION);
//            reviewDetailText.setViewTypeId(VIEW_TYPE_ID_DETAIL_OVERVIEW);
//            mListViewType.add(reviewDetailText);
//        } else {
            mListViewType.add(VIEW_TYPE_OVERVIEW);
//        }

//        SpecDao specDao = productDetailNew.getSpecDao();
//        specDao.setViewTypeId(VIEW_TYPE_ID_DETAIL_SPEC);
//        mListViewType.add(specDao);
//
        mListViewType.add(VIEW_TYPE_SPEC);
//        mListViewType.add(VIEW_TYPE_PROMOTION);
//        mListViewType.add(VIEW_TYPE_DELIVERY);

        notifyDataSetChanged();

    }

//    public void setProductRecommend(Recommend recommend) {
//        recommend.setViewTypeId(VIEW_TYPE_ID_RECOMMEND_ITEM);
//        mListViewType.add(recommend);
//        notifyDataSetChanged();
//    }

    public void updateProductDetailOption(ProductDetail productDetail, ProductDetailOptionItem productDetailOptionItem, int adapterPosition) {
        ProductDetailOptionItem selectedProductDetailOptionItem = productDetailOptionItem;
        if (selectedProductDetailOptionItem.isAvailableOptionAvailable()) {
            productDetail.setViewTypeId(VIEW_TYPE_ID_DETAIL_ITEM);
            mListViewType.set(adapterPosition, productDetail);
            notifyItemChanged(adapterPosition);
        } else {
            ProductDetailImage productDetailImage = selectedProductDetailOptionItem.getProductDetailImage();
            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
            mListViewType.set(0, productDetailImage);
            notifyItemChanged(0);

            productDetail.setViewTypeId(VIEW_TYPE_ID_DETAIL_ITEM);
            mListViewType.set(adapterPosition, productDetail);
            notifyItemChanged(adapterPosition);
        }

    }

    public void updateProductDetailImage(ProductDetail productDetail, ProductDetailAvailableOptionItem productDetailAvailableOptionItem, int adapterPosition, int adapterItem) {
        ProductDetailAvailableOptionItem selectProductAvaileOptionItem = productDetailAvailableOptionItem;
        if (selectProductAvaileOptionItem.isSelected()) {
            ProductDetailImage productDetailImage = selectProductAvaileOptionItem.getProductDetailImage();
            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
            mListViewType.set(adapterPosition, productDetailImage);
            notifyItemChanged(adapterPosition);

            productDetail.setViewTypeId(VIEW_TYPE_ID_DETAIL_ITEM);
            mListViewType.set(adapterItem, productDetail);
            notifyItemChanged(adapterItem);

        } else {
            ProductDetailImage productDetailImage = productDetail.getProductImageList();
            productDetailImage.setViewTypeId(VIEW_TYPE_ID_IMAGE);
            mListViewType.set(adapterPosition, productDetailImage);
            notifyItemChanged(adapterPosition);

            productDetail.setViewTypeId(VIEW_TYPE_ID_DETAIL_ITEM);
            mListViewType.set(adapterItem, productDetail);
            notifyItemChanged(adapterItem);

        }
    }

    public void showLoading() {
        //mListViewType.add(VIEW_TYPE_LOADING);
        isLoadingShow = true;
        //notifyItemInserted(0);
    }

    public void setError() {
        if (isLoadingShow) {
            isLoadingShow = false;
            mListViewType.clear();
            notifyDataSetChanged();
            mListViewType.add(VIEW_TYPE_RESULT);
            notifyItemInserted(0);
            notifyItemRangeInserted(0, mListViewType.size());
        }else {
            mListViewType.clear();
            notifyDataSetChanged();
            mListViewType.add(VIEW_TYPE_RESULT);
            notifyItemInserted(0);
            notifyItemRangeInserted(0, mListViewType.size());
        }
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
