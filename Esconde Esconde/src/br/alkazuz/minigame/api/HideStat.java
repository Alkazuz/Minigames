package br.alkazuz.minigame.api;

public class HideStat {

	private String player;
	  
	  private boolean hidden = false;
	  
	  public HideStat(String p, boolean hid) {
	    this.player = p;
	    this.hidden = hid;
	  }
	  
	  public boolean isHidden() {
	    return this.hidden;
	  }
	  
	  public String getName() {
	    return this.player;
	  }
	  
	  public void setHidden(boolean hide) {
	    this.hidden = hide;
	  }
	
}
