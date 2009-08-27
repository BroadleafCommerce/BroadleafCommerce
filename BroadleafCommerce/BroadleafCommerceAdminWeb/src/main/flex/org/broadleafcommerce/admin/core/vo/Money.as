package org.broadleafcommerce.admin.core.vo
{
	import flash.utils.IDataInput;
	import flash.utils.IDataOutput;
	import flash.utils.IExternalizable;
	
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.util.money.Money")]	
	public class Money implements IExternalizable
	{
		public var amount:Number = new Number(); 
		public var currency:Object = new Object();

    public function readExternal(input:IDataInput):void {
    	var x:Number = input.readFloat();
		amount = x;// input.readFloat();
		// currency = input.readObject();
    }

    public function writeExternal(output:IDataOutput):void {
        output.writeFloat(amount);
        // output.writeObject(currency);
    }

	}
}