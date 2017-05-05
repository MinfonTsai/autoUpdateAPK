package com.test.autoupdateapk;

public class apklist {

	  private  int  id;  
	  private String apkName;  
	  private String className;
	  private String pkgName;
	  private int    key;
	  private String urlLink;
	  private String pubdate;
	  private String version; 
	  private String description; 
	  
	  public apklist(int id,String apkName,String className,String pkgName,int key, String urlLink, String pubdate ,String version,String description ){  
	      this.id=id;  
	      this.apkName=apkName;  
	      this.className=className;
	      this.pkgName=pkgName;  
	      this.key=key;
	      this.urlLink=urlLink;
	      this.pubdate=pubdate;  
	      this.version=version;  
	      this.description=description;
	  }
	  
	    public int getid() {  
		    return id;  
		}  
		public void setid(int id) {  
		    this.id = id;  
		}  
		public String getapkName() {  
		    return apkName;  
		}
		public String getclassName() {  
		    return className;  
		} 
		public String getpkgName() {  
		    return pkgName;  
		} 
		public String geturlLink() {  
		    return urlLink;  
		} 
		public String getdescroiption() {  
		    return description;  
		} 
		public void setapkName(String apkname) {  
		    this.apkName = apkname;  
		}
		public String getversion() {  
		    return version;  
		}  
		public void setversion(String version) {  
		    this.version = version;  
		}
}
