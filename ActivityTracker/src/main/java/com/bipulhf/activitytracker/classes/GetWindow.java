package com.bipulhf.activitytracker.classes;

import java.io.*;
import java.util.Objects;

public class GetWindow {
    final static String cmd = GetList.getDirectoryName() + "\\GetWindowsTitle.ps1";
    final static File f = new File(cmd);

    public static String getTitle() throws Exception{
        Process p;
        if(Objects.equals(System.getProperty("os.name"), "Linux"))
            p = Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c","xprop -root _NET_ACTIVE_WINDOW | awk -F' ' '{print $NF}' | xargs xprop -id | grep -i wm_name | awk -F'=' 'NR==2 {print $2}' | sed 's/\\\"//g'"});
        else {
            if(!f.exists() && !f.isDirectory())
                getCommand();
            p = Runtime.getRuntime().exec("powershell.exe powershell -ExecutionPolicy ByPass -File " + cmd);
        }
//        Process p = Runtime.getRuntime().exec("javac file_name.java && java file_name");
        BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        String output = "";
        while ((line = bf.readLine()) != null) {
            output = line;
        }
        return output.strip();
    }

    private static void getCommand() {
        String s = "Add-Type  @\"\n" +
                "             using System;\n" +
                "             using System.Runtime.InteropServices;\n" +
                "             using System.Text;\n" +
                "            public class APIFuncs\n" +
                "               {\n" +
                "                [DllImport(\"user32.dll\", CharSet = CharSet.Auto, SetLastError = true)]\n" +
                "               public static extern int GetWindowText(IntPtr hwnd,StringBuilder\n" +
                "            lpString, int cch);\n" +
                "                [DllImport(\"user32.dll\", SetLastError=true, CharSet=CharSet.Auto)]\n" +
                "               public static extern IntPtr GetForegroundWindow();\n" +
                "                [DllImport(\"user32.dll\", SetLastError=true, CharSet=CharSet.Auto)]\n" +
                "                   public static extern Int32 GetWindowThreadProcessId(IntPtr hWnd,out\n" +
                "            Int32 lpdwProcessId);\n" +
                "                [DllImport(\"user32.dll\", SetLastError=true, CharSet=CharSet.Auto)]\n" +
                "                   public static extern Int32 GetWindowTextLength(IntPtr hWnd);\n" +
                "                }\n" +
                "\"@\n" +
                "            $w = [apifuncs]::GetForegroundWindow()\n" +
                "            $len = [apifuncs]::GetWindowTextLength($w)\n" +
                "            $sb = New-Object text.stringbuilder -ArgumentList ($len + 1)\n" +
                "            $rtnlen = [apifuncs]::GetWindowText($w,$sb,$sb.Capacity)\n" +
                "            write-host $($sb.tostring())";
        try (PrintWriter out = new PrintWriter(cmd)) {
            out.println(s);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
