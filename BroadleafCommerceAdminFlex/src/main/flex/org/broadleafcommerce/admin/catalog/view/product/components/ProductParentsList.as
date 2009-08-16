package org.broadleafcommerce.admin.catalog.view.product.components
{
	import flash.display.Sprite;
	import flash.events.KeyboardEvent;
	
	import mx.controls.CheckBox;
	import mx.controls.List;
	import mx.controls.listClasses.IListItemRenderer;
	import mx.core.ClassFactory;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class ProductParentsList extends List
	{
		public function ProductParentsList()
		{
			super();
			this.itemRenderer = new ClassFactory(ProductParentListRenderer);
		}

	// fake all mouse interaction as if it had the ctrl key down
	override protected function selectItem(item:IListItemRenderer,
                                  shiftKey:Boolean, ctrlKey:Boolean,
                                  transition:Boolean = true):Boolean
	{
		return super.selectItem(item, false, true, transition);
	}
	
	// turn off selection indicator
    override protected function drawSelectionIndicator(
                                indicator:Sprite, x:Number, y:Number,
                                width:Number, height:Number, color:uint,
                                itemRenderer:IListItemRenderer):void
    {
	}

	// whenever we draw the renderer, make sure we re-eval the checked state
    override protected function drawItem(item:IListItemRenderer,
                                selected:Boolean = false,
                                highlighted:Boolean = false,
                                caret:Boolean = false,
                                transition:Boolean = false):void
    {
		CheckBox(item).invalidateProperties();
		super.drawItem(item, selected, highlighted, caret, transition);
	}

	// fake all keyboard interaction as if it had the ctrl key down
	override protected function keyDownHandler(event:KeyboardEvent):void
	{
		// this is technically illegal, but works
		event.ctrlKey = true;
		event.shiftKey = false;
		super.keyDownHandler(event);
	}

		
	}
}