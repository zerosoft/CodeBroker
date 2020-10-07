package com.codebroker.web.api.util;

public class Platform
{
	public static final int	OS_				= 0;	// Others
	public static final int	OS_Linux		= 1;	// Linux
	public static final int	OS_Mac			= 2;	// Mac OS, Mac OS X
	public static final int	OS_Window		= 3;	// Windows .+
	public static final int	OS_OS_2			= 4;	// OS/2
	public static final int	OS_Solaris		= 5;	// Solaris
	public static final int	OS_SunOS		= 6;	// Sun OS
	public static final int	OS_MPE_iX		= 7;	// MPE/ix
	public static final int	OS_HP_UX		= 8;	// HP-UX
	public static final int	OS_AIX			= 9;	// AIX
	public static final int	OS_OS_390		= 10;	// OS/390
	public static final int	OS_FreeBSD		= 11;	// FreeBSD
	public static final int	OS_Irix			= 12;	// Irix
	public static final int	OS_Digital_Unix	= 13;	// Digital Unix
	public static final int	OS_NetWare		= 14;	// NetWare 4.11
	public static final int	OS_OSF1			= 15;	// OSF1
	public static final int	OS_OpenVMS		= 16;	// OpenVMS
	
	
	private static String	name	= System.getProperty("os.name");
	private static String	version	= System.getProperty("os.version");
	private static String	arch	= System.getProperty("os.arch");
	private static int		os		= OS_Window;
	
	static
	{
		String	tmp	= name.toLowerCase();
		if(tmp.contains("linux"))
		{
			os	= OS_Linux;
		}
		else if(tmp.contains("mac") && tmp.contains("os"))
		{
			os	= OS_Mac;
		}
		else if(tmp.contains("win"))
		{
			os	= OS_Window;
		}
		else if(tmp.equals("os/2"))
		{
			os	= OS_OS_2;
		}
		else if(tmp.contains("solaris"))
		{
			os	= OS_Solaris;
		}
		else if(tmp.contains("sunos"))
		{
			os	= OS_SunOS;
		}
		else if(tmp.contains("mpe/ix"))
		{
			os	= OS_MPE_iX;
		}
		else if(tmp.contains("hp-ux"))
		{
			os	= OS_HP_UX;
		}
		else if(tmp.contains("aix"))
		{
			os	= OS_AIX;
		}
		else if(tmp.contains("os/390"))
		{
			os	= OS_OS_390;
		}
		else if(tmp.contains("freebsd"))
		{
			os	= OS_FreeBSD;
		}
		else if(tmp.contains("irix"))
		{
			os	= OS_Irix;
		}
		else if(tmp.contains("digital unix"))
		{
			os	= OS_Digital_Unix;
		}
		else if(tmp.contains("netware"))
		{
			os	= OS_NetWare;
		}
		else if(tmp.contains("osf1"))
		{
			os	= OS_OSF1;
		}
		else if(tmp.contains("openvms"))
		{
			os	= OS_OpenVMS;
		}
		else if(tmp.equals(""))
		{
			os	= OS_;
		}
	}
	
	private Platform()
	{
		//
	}
	
	public static String name()
	{
		return name;
	}
	
	public static String version()
	{
		return version;
	}
	
	public static String arch()
	{
		return arch;
	}
	
	public static boolean isLinux()
	{
		return os == OS_Linux;
	}
	
	public static boolean isWindows()
	{
		return os == OS_Window;
	}
	
	public static boolean isMac()
	{
		return os == OS_Mac;
	}
	
	public static int availableProcessors()
	{
		return Runtime.getRuntime().availableProcessors();
	}
	
}