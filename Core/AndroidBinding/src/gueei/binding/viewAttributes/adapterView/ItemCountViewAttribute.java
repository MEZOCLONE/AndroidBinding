package gueei.binding.viewAttributes.adapterView;

import java.util.Collection;

import android.database.DataSetObserver;
import android.widget.AdapterView;
import gueei.binding.Binder;
import gueei.binding.IObservable;
import gueei.binding.Observer;
import gueei.binding.ViewAttribute;
import gueei.binding.exception.AttributeNotDefinedException;
import android.widget.Adapter;

/**
 * Number of Item in the Adapter View
 * Returns AdapterView.getCount()
 * 
 * @name itemCount
 * @widget AdapterView
 * @type Integer
 * @accepts	Integer
 * @category list
 * @related 
 * 
 * @author andy
 */
public class ItemCountViewAttribute extends ViewAttribute<AdapterView<?>, Integer> {

	public ItemCountViewAttribute(AdapterView<?> view) {
		super(Integer.class, view, "itemCount");
		try {
			adapterAttr = Binder.getAttributeForView(view, "adapter");
			adapterAttr.subscribe(adapterObserver);
			lastAdapter = ((Adapter)adapterAttr.get());
			onAdapterChanged((Adapter)adapterAttr.get());
		} catch (AttributeNotDefinedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void onAdapterChanged(Adapter newAdapter){
		if (lastAdapter!=null){
			lastAdapter.unregisterDataSetObserver(dataSetObserver);
		}
		lastAdapter = newAdapter;
		if(lastAdapter!=null)
			lastAdapter.registerDataSetObserver(dataSetObserver);
	}
	
	private ViewAttribute<?,?> adapterAttr;
	private Adapter lastAdapter = null;
	private Observer adapterObserver = new Observer(){
		@Override
		public void onPropertyChanged(IObservable<?> prop,
				Collection<Object> initiators) {
			onAdapterChanged((Adapter)adapterAttr.get());
			initiators.add(this);
			notifyChanged(initiators);
		}
	};
	
	private DataSetObserver dataSetObserver = new DataSetObserver(){
		@Override
		public void onChanged() {
			super.onChanged();
			notifyChanged();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			notifyChanged();
		}
	};
	
	@Override
	protected void doSetAttributeValue(Object newValue) {
		// Readonly
	}

	@Override
	public Integer get() {
		if(getView()==null || getView().getAdapter()==null) return null;
		return getView().getAdapter().getCount();
	}

}