////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package
{

/**
 *  @private
 *  This class is used to link additional classes into airframework.swc
 *  beyond those that are found by dependecy analysis starting
 *  from the classes specified in manifest.xml.
 */
internal class AIRFrameworkClasses
{
	import mx.managers.NativeDragManagerImpl; NativeDragManagerImpl;
	import mx.skins.halo.StatusBarBackgroundSkin; StatusBarBackgroundSkin;
	import mx.skins.halo.WindowBackground; WindowBackground
	import mx.skins.halo.ApplicationTitleBarBackgroundSkin; ApplicationTitleBarBackgroundSkin;
	import mx.skins.halo.WindowCloseButtonSkin; WindowCloseButtonSkin;
	import mx.skins.halo.WindowMinimizeButtonSkin; WindowMinimizeButtonSkin;
	import mx.skins.halo.WindowMaximizeButtonSkin; WindowMaximizeButtonSkin;
	import mx.skins.halo.WindowRestoreButtonSkin; WindowRestoreButtonSkin;
}

}
