////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
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
 *  This class is used to link additional classes into framework.swc
 *  beyond those that are found by dependecy analysis starting
 *  from the classes specified in manifest.xml.
 *  For example, Button does not have a reference to ButtonSkin,
 *  but ButtonSkin needs to be in framework.swc along with Button.
 */
internal class FrameworkClasses
{
	import mx.binding.ArrayElementWatcher; ArrayElementWatcher;
	import mx.binding.BindabilityInfo; BindabilityInfo;
	import mx.binding.EvalBindingResponder; EvalBindingResponder;
	import mx.binding.FunctionReturnWatcher; FunctionReturnWatcher;
	import mx.binding.IBindingClient; IBindingClient;
	import mx.binding.IWatcherSetupUtil; IWatcherSetupUtil;
	import mx.binding.PropertyWatcher; PropertyWatcher;
	import mx.binding.RepeatableBinding; RepeatableBinding;
	import mx.binding.RepeaterComponentWatcher; RepeaterComponentWatcher;
	import mx.binding.RepeaterItemWatcher; RepeaterItemWatcher;
	import mx.binding.StaticPropertyWatcher; StaticPropertyWatcher;
	import mx.binding.XMLWatcher; XMLWatcher;
	import mx.binding.utils.BindingUtils; BindingUtils;
	import mx.binding.utils.ChangeWatcher; ChangeWatcher;
	import mx.controls.Alert; Alert;
	import mx.controls.videoClasses.CuePointManager; CuePointManager;
	import mx.core.BitmapAsset; BitmapAsset;
	import mx.core.ButtonAsset; ButtonAsset;
	import mx.core.ByteArrayAsset; ByteArrayAsset;
	import mx.core.ClassFactory; ClassFactory;
	import mx.core.CrossDomainRSLItem; CrossDomainRSLItem;
	import mx.core.DeferredInstanceFromClass; DeferredInstanceFromClass;
	import mx.core.DeferredInstanceFromFunction; DeferredInstanceFromFunction;
	import mx.core.FlexLoader; FlexLoader;
	import mx.core.FontAsset; FontAsset;
	import mx.core.IDeferredInstance; IDeferredInstance;
	import mx.core.IUID;
	import mx.core.MovieClipAsset; MovieClipAsset;
	import mx.core.MovieClipLoaderAsset; MovieClipLoaderAsset;
	import mx.core.MXMLObjectAdapter; MXMLObjectAdapter;
	import mx.core.SimpleApplication; SimpleApplication;
	import mx.core.SoundAsset; SoundAsset;
	import mx.core.TextFieldAsset; TextFieldAsset;
	import mx.effects.easing.Back; Back;
	import mx.effects.easing.Bounce; Bounce;
	import mx.effects.easing.Circular; Circular;
	import mx.effects.easing.Cubic; Cubic;
	import mx.effects.easing.Elastic; Elastic;
	import mx.effects.easing.Exponential; Exponential;
	import mx.effects.easing.Linear; Linear;
	import mx.effects.easing.Quadratic; Quadratic;
	import mx.effects.easing.Quartic; Quartic;
	import mx.effects.easing.Quintic; Quintic;
	import mx.effects.easing.Sine; Sine;
	import mx.events.ModuleEvent; ModuleEvent;
	import mx.graphics.ImageSnapshot; ImageSnapshot;
	import mx.graphics.codec.PNGEncoder; PNGEncoder;
	import mx.graphics.codec.JPEGEncoder; JPEGEncoder;
	import mx.logging.ILogger; ILogger;
	import mx.logging.Log; Log;
	import mx.logging.targets.TraceTarget; TraceTarget;
	import mx.managers.DragManager; DragManager;
	import mx.modules.IModuleInfo; IModuleInfo;
	import mx.modules.Module; Module;
	import mx.modules.ModuleBase; ModuleBase;
	import mx.modules.ModuleLoader; ModuleLoader;
	import mx.modules.ModuleManager; ModuleManager;
	import mx.preloaders.DownloadProgressBar; DownloadProgressBar;
	import mx.printing.FlexPrintJob; FlexPrintJob;
	import mx.resources.Locale; Locale;
	import mx.rpc.IResponder; IResponder;
	import mx.skins.Border; Border;
	import mx.skins.halo.AccordionHeaderSkin; AccordionHeaderSkin;
	import mx.skins.halo.ActivatorSkin; ActivatorSkin;
	import mx.skins.halo.ApplicationBackground; ApplicationBackground;
	import mx.skins.halo.BrokenImageBorderSkin; BrokenImageBorderSkin;
	import mx.skins.halo.BusyCursor; BusyCursor;
	import mx.skins.halo.ButtonBarButtonSkin; ButtonBarButtonSkin;
	import mx.skins.halo.ButtonSkin; ButtonSkin;
	import mx.skins.halo.CheckBoxIcon; CheckBoxIcon;
	import mx.skins.halo.ColorPickerSkin; ColorPickerSkin;
	import mx.skins.halo.ComboBoxArrowSkin; ComboBoxArrowSkin;
	import mx.skins.halo.DataGridColumnResizeSkin; DataGridColumnResizeSkin;
	import mx.skins.halo.DataGridHeaderBackgroundSkin; DataGridHeaderBackgroundSkin;
	import mx.skins.halo.DataGridHeaderSeparator; DataGridHeaderSeparator;
	import mx.skins.halo.DataGridSortArrow; DataGridSortArrow;
	import mx.skins.halo.DateChooserIndicator; DateChooserIndicator;
	import mx.skins.halo.DateChooserMonthArrowSkin; DateChooserMonthArrowSkin;
	import mx.skins.halo.DateChooserYearArrowSkin; DateChooserYearArrowSkin;
	import mx.skins.halo.DefaultDragImage; DefaultDragImage;
	import mx.skins.halo.HaloBorder; HaloBorder;
	import mx.skins.halo.HaloColors; HaloColors;
	import mx.skins.halo.HaloFocusRect; HaloFocusRect;
	import mx.skins.halo.SliderHighlightSkin; SliderHighlightSkin;
	import mx.skins.halo.SliderThumbSkin; SliderThumbSkin;
	import mx.skins.halo.SliderTrackSkin; SliderTrackSkin;
	import mx.skins.halo.LinkButtonSkin; LinkButtonSkin;
	import mx.skins.halo.LinkSeparator; LinkSeparator;
	import mx.skins.halo.ListDropIndicator; ListDropIndicator;
	import mx.skins.halo.MenuBarBackgroundSkin; MenuBarBackgroundSkin;
	import mx.skins.halo.NumericStepperDownSkin; NumericStepperDownSkin;
	import mx.skins.halo.NumericStepperUpSkin; NumericStepperUpSkin;
	import mx.skins.halo.PanelSkin; PanelSkin;
	import mx.skins.halo.PopUpButtonSkin; PopUpButtonSkin;
	import mx.skins.halo.PopUpIcon; PopUpIcon;
	import mx.skins.halo.PopUpMenuIcon; PopUpMenuIcon;
	import mx.skins.halo.ProgressBarSkin; ProgressBarSkin;
	import mx.skins.halo.ProgressIndeterminateSkin; ProgressIndeterminateSkin;
	import mx.skins.halo.ProgressMaskSkin; ProgressMaskSkin;
	import mx.skins.halo.ProgressTrackSkin; ProgressTrackSkin;
	import mx.skins.halo.RadioButtonIcon; RadioButtonIcon;
	import mx.skins.halo.ScrollArrowSkin; ScrollArrowSkin;
	import mx.skins.halo.ScrollThumbSkin; ScrollThumbSkin;
	import mx.skins.halo.ScrollTrackSkin; ScrollTrackSkin;
	import mx.skins.halo.TabSkin; TabSkin;
	import mx.skins.halo.TitleBackground; TitleBackground;
	import mx.skins.halo.ToolTipBorder; ToolTipBorder;
	import mx.skins.ProgrammaticSkin; ProgrammaticSkin;
	import mx.skins.RectangularBorder; RectangularBorder;
	import mx.styles.IStyleModule; IStyleModule;
	import mx.utils.ArrayUtil; ArrayUtil;
	import mx.utils.DescribeTypeCache; DescribeTypeCache;
	import mx.utils.DescribeTypeCacheRecord; DescribeTypeCacheRecord;
	import mx.utils.DisplayUtil; DisplayUtil;
	import mx.utils.XMLUtil; XMLUtil;
	import mx.utils.Base64Decoder; Base64Decoder;
	import mx.utils.Base64Encoder; Base64Encoder;
	import mx.validators.Validator; Validator;
	// Maintain alphabetical order
}

}

