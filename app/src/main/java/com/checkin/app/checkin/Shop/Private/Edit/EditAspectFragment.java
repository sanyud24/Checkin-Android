package com.checkin.app.checkin.Shop.Private.Edit;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.ShopProfileViewModel;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.misc.adapters.StatusTextViewHolder;
import com.checkin.app.checkin.misc.views.MultiSpinner;
import com.checkin.app.checkin.utility.HeaderFooterRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Jogi Miglani on 18-10-2018.
 */

public class EditAspectFragment extends Fragment implements MultiSpinner.MultiSpinnerListener {
    private static final String TAG = EditAspectFragment.class.getSimpleName();
    @BindView(R.id.cb_choice_paytm)
    CompoundButton cbChoicePaytm;
    @BindView(R.id.cb_choice_card)
    CompoundButton cbChoiceCard;
    @BindView(R.id.cb_choice_cash)
    CompoundButton cbChoiceCash;
    @BindView(R.id.rb_alcohol_yes)
    CompoundButton rbChoiceAlcoholYes;
    @BindView(R.id.rb_alcohol_no)
    CompoundButton rbChoiceAlcoholNo;
    @BindView(R.id.rb_delivery_no)
    CompoundButton rbChoiceDeliveryNo;
    @BindView(R.id.rb_delivery_yes)
    CompoundButton rbChoiceDeliveryYes;
    @BindView(R.id.rb_non_veg)
    CompoundButton rbChoiceNonVeg;
    @BindView(R.id.rb_veg)
    CompoundButton rbChoiceVeg;
    @BindView(R.id.rv_additional_data)
    RecyclerView rvExtraData;
    @BindView(R.id.spinner_cuisines)
    MultiSpinner vCuisines;
    @BindView(R.id.spinner_categories)
    MultiSpinner vCategories;
    private Unbinder unbinder;
    private AdditionalDataAdapter mAdapter;
    private ShopProfileViewModel mViewModel;
    private AspectFragmentInteraction mListener;

    public static EditAspectFragment newInstance(AspectFragmentInteraction listener) {
        EditAspectFragment fragment = new EditAspectFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_edit_aspect, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvExtraData.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
        mAdapter = new AdditionalDataAdapter(null);
        rvExtraData.setAdapter(mAdapter);

        if (getActivity() == null)
            return;

        vCuisines.setListener(this);
        vCategories.setListener(this);

        mViewModel = ViewModelProviders.of(getActivity()).get(ShopProfileViewModel.class);

        mViewModel.getShopData().observe(this, resource -> {
            if (resource == null)
                return;

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                this.setupValues(resource.getData());
            }
        });

        mViewModel.getErrors().observe(this, errors -> {
            if (errors == null)
                return;
            if (errors.has("extra_data")) {
                Toast.makeText(getContext(), "Ensure none of the additional data exceeds 50 characters.", Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.shouldCollectAspectData().observe(this, shouldCollectData -> {
            if (shouldCollectData != null && shouldCollectData) {
                CharSequence[] cuisines = vCuisines.getSelectedValues();
                CharSequence[] categories = vCategories.getSelectedValues();
                PAYMENT_MODE[] modes = this.getPaymentModes();
                boolean hasNonVeg = rbChoiceNonVeg.isChecked();
                boolean hasAlcohol = rbChoiceAlcoholYes.isChecked();
                boolean hasHomeDelivery = rbChoiceDeliveryYes.isChecked();
                List<String> extraData = mAdapter.getData();
                RestaurantModel shop = mViewModel.updateAspectData(
                        cuisines, categories, modes, hasNonVeg, hasAlcohol, hasHomeDelivery, extraData
                );
                mListener.updateShopAspects(shop);
            }
        });
    }

    private void setupValues(RestaurantModel shop) {
        mAdapter.setData(new ArrayList<>(Arrays.asList(shop.getExtraData())));
        vCategories.selectValues(shop.getCategories());
        vCuisines.selectValues(shop.getCuisines());
        setPaymentModes(shop.getPaymentModes());

        if (shop.servesAlcohol())
            rbChoiceAlcoholYes.setChecked(true);
        else
            rbChoiceAlcoholNo.setChecked(true);

        if (shop.servesNonveg())
            rbChoiceNonVeg.setChecked(true);
        else
            rbChoiceVeg.setChecked(true);

        if (shop.hasHomeDelivery())
            rbChoiceDeliveryYes.setChecked(true);
        else
            rbChoiceDeliveryNo.setChecked(true);
    }

    private PAYMENT_MODE[] getPaymentModes() {
        List<PAYMENT_MODE> modes = new ArrayList<>();
        if (cbChoiceCash.isChecked())
            modes.add(PAYMENT_MODE.CASH);
        if (cbChoiceCard.isChecked())
            modes.add(PAYMENT_MODE.CARD);
        if (cbChoicePaytm.isChecked())
            modes.add(PAYMENT_MODE.PAYTM);
        return modes.toArray(new PAYMENT_MODE[]{});
    }

    private void setPaymentModes(PAYMENT_MODE[] modes) {
        for (PAYMENT_MODE mode : modes) {
            switch (mode) {
                case CASH:
                    cbChoiceCash.setChecked(true);
                    break;
                case CARD:
                    cbChoiceCard.setChecked(true);
                    break;
                case PAYTM:
                    cbChoicePaytm.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = ((AspectFragmentInteraction) context);
        } catch (ClassCastException ignored) {
        }
    }


    @OnClick({R.id.cb_choice_cash, R.id.cb_choice_card, R.id.cb_choice_paytm})
    public void onPaymentChoice(CompoundButton cb) {
        mListener.onAspectDataValidStatus(isDataValid());
    }

    @Override
    public void onItemsSelected(boolean[] selected) {
        mListener.onAspectDataValidStatus(isDataValid());
    }

    private boolean isDataValid() {
        boolean validPaymentMode = cbChoiceCash.isChecked() || cbChoiceCard.isChecked() || cbChoicePaytm.isChecked();
        boolean validCuisines = vCuisines.getSelectedValues().length > 0;
        boolean validCategories = vCategories.getSelectedValues().length > 0;
        return validCategories && validCuisines && validPaymentMode;
    }

    public interface AspectFragmentInteraction {
        void updateShopAspects(RestaurantModel shop);

        void onAspectDataValidStatus(boolean isValid);
    }

    class AdditionalDataAdapter extends HeaderFooterRecyclerViewAdapter {
        private List<String> mData;
        private AlertDialog mAddDialog;

        AdditionalDataAdapter(List<String> data) {
            mData = data;
            setupAddDialog();
        }

        public List<String> getData() {
            return mData;
        }

        public void setData(List<String> data) {
            mData = data;
            notifyDataSetChanged();
        }

        public void addData(String data) {
            mData.add(data);
            notifyItemInserted(getBasicItemCount() - 1);
        }

        public void removeData(String data) {
            int index = mData.indexOf(data);
            mData.remove(index);
            notifyItemRemoved(index);
        }

        @Override
        public boolean useHeader() {
            return false;
        }

        @Override
        public boolean useFooter() {
            return true;
        }

        @Override
        public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new StatusTextViewHolder(view);
        }

        @Override
        public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
            StatusTextViewHolder viewHolder = ((StatusTextViewHolder) holder);
            viewHolder.bindData(mData.get(position), R.drawable.ic_action_remove);
            viewHolder.setStatusClickListener(v -> removeData(viewHolder.getData()));
        }

        @Override
        public int getBasicItemCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public int getBasicItemType(int position) {
            return R.layout.item_status_text;
        }

        @Override
        public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status_text, parent, false);
            return new StatusTextViewHolder(view);
        }

        private void setupAddDialog() {
            EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            mAddDialog = builder
                    .setTitle("Add detail")
                    .setView(input)
                    .setPositiveButton("Add", (dialog, which) -> {
                        this.addData(input.getText().toString());
                        input.setText("");
                    })
                    .setNegativeButton("Cancel", ((dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        input.setText("");
                    }))
                    .create();
        }

        @Override
        public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {
            StatusTextViewHolder viewHolder = (StatusTextViewHolder) holder;
            viewHolder.bindData("More", R.drawable.ic_action_add);
            viewHolder.setStatusClickListener(v -> mAddDialog.show());
        }
    }
}
