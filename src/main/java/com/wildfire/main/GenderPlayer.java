package com.wildfire.main;

import com.google.common.hash.Hashing;
import com.wildfire.physics.BreastPhysics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class GenderPlayer {

	public String username;
	public int gender;
	public float pBustSize = 0.6f;

	public boolean hurtSounds = true;


	//physics variables
	public boolean breast_physics = false;
	public boolean breast_physics_armor = false;
	public float bounceMultiplier = 0.34f;
	public float floppyMultiplier = 0.95f;

	public String preCapeURL = "";
	public boolean lockSettings = false;

	public SyncStatus syncStatus = SyncStatus.UNKNOWN;
	public boolean show_in_armor = false;

	private Configuration cfg;
	private BreastPhysics lBreastPhysics, rBreastPhysics;
	private Breasts breasts;

	
	public GenderPlayer(String username) {
		this(username, 1);
	}
	public GenderPlayer(String username, int gender) {
		lBreastPhysics = new BreastPhysics(this);
		rBreastPhysics = new BreastPhysics(this);
		breasts = new Breasts();
		this.username = username;
		this.gender = gender;
		this.cfg = new Configuration("WildfireGender", this.username);
		this.cfg.setDefaultParameter("username", "NOT_AVAILABLE");
		this.cfg.setDefaultParameter("gender", 1);
		this.cfg.setDefaultParameter("bust_size", 0.6f);
		this.cfg.setDefaultParameter("cape_url", "");
		this.cfg.setDefaultParameter("show_elytra", true);
		this.cfg.setDefaultParameter("hurt_sounds", true);


		this.cfg.setDefaultParameter("breasts_xOffset", 0);
		this.cfg.setDefaultParameter("breasts_yOffset", 0);
		this.cfg.setDefaultParameter("breasts_zOffset", 0);
		this.cfg.setDefaultParameter("breasts_uniboob", true);
		this.cfg.setDefaultParameter("breasts_cleavage", "0.05");

		this.cfg.setDefaultParameter("breast_physics", false);
		this.cfg.setDefaultParameter("breast_physics_armor", false);
		this.cfg.setDefaultParameter("show_in_armor", true);
		this.cfg.setDefaultParameter("bounce_multiplier", "0.34");
		this.cfg.setDefaultParameter("floppy_multiplier", "0.95");
		this.cfg.finish();
	}

	//send to server
	public void sendNetwork() {

	}

	//update from server
	public void getNetwork() {

	}
	public Configuration getConfig() {
		return cfg;
	}
	public float getBustSize() {
		return pBustSize;
	}

	public float getBounceMultiplier() {
		float bounceMult = Float.valueOf(this.bounceMultiplier);
		return Math.round((bounceMult * 3) * 100) / 100f;
	}

	public float getFloppiness() {
		return Float.valueOf(this.floppyMultiplier);
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}
	@SuppressWarnings("unchecked")
	public void updateBustSize(float v) {
		this.pBustSize = v;
	}
	public static JSONObject toJSONObject(GenderPlayer plr) {
		JSONObject obj = new JSONObject();
		obj.put("username", plr.username);
		obj.put("gender", plr.gender);
		obj.put("bust_size", plr.pBustSize);
		obj.put("hurt_sounds", plr.hurtSounds);

		obj.put("breast_physics", plr.breast_physics);
		obj.put("breast_physics_armor", plr.breast_physics_armor);
		obj.put("show_in_armor", plr.show_in_armor);
		obj.put("bounce_multiplier", plr.bounceMultiplier);
		obj.put("floppy_multiplier", plr.floppyMultiplier);

		obj.put("breasts_xOffset", plr.getBreasts().xOffset);
		obj.put("breasts_yOffset", plr.getBreasts().yOffset);
		obj.put("breasts_zOffset", plr.getBreasts().zOffset);
		obj.put("breasts_uniboob", plr.getBreasts().isUniboob);
		obj.put("breasts_cleavage", plr.getBreasts().cleavage);
		return obj;
	}
	public void toJSONObject() {
		toJSONObject(this);
	}
	public static GenderPlayer fromJSONObject(JSONObject obj) {
		GenderPlayer plr = new GenderPlayer(obj.get("username").toString());
		plr.gender = Integer.valueOf(obj.get("gender").toString());
		plr.pBustSize = Float.valueOf(obj.get("bust_size").toString());
		plr.hurtSounds = Boolean.valueOf(obj.get("hurt_sounds").toString());

		//physics
		plr.breast_physics = Boolean.valueOf(obj.get("breast_physics").toString());
		plr.breast_physics_armor = Boolean.valueOf(obj.get("breast_physics_armor").toString());
		plr.show_in_armor = Boolean.valueOf(obj.get("show_in_armor").toString());
		plr.bounceMultiplier = Float.valueOf(obj.get("bounce_multiplier").toString());
		plr.floppyMultiplier = Float.valueOf(obj.get("floppy_multiplier").toString());

		plr.getBreasts().xOffset = Float.valueOf(obj.get("breasts_xOffset").toString());
		plr.getBreasts().yOffset = Float.valueOf(obj.get("breasts_yOffset").toString());
		plr.getBreasts().zOffset = Float.valueOf(obj.get("breasts_zOffset").toString());
		plr.getBreasts().isUniboob = Boolean.valueOf(obj.get("breasts_uniboob").toString());
		plr.getBreasts().cleavage = Float.valueOf(obj.get("breasts_cleavage").toString());

		return plr;
	}


	public static GenderPlayer loadCachedPlayer(String uuid) {
		GenderPlayer plr = WildfireGender.getPlayerByName(uuid);
		if (plr != null) {
			plr.lockSettings = false;
			plr.syncStatus = SyncStatus.CACHED;
			try {
				plr.gender = Integer.valueOf(plr.getConfig().getParameter("gender").toString());
			} catch(Exception e) {
				plr.gender = Boolean.valueOf(plr.getConfig().getParameter("gender").toString())?1:0;
			}
			plr.updateBustSize(Float.valueOf(plr.getConfig().getParameter("bust_size").toString()));
			plr.hurtSounds = plr.getConfig().getBool("hurt_sounds");

			//physics
			plr.breast_physics = plr.getConfig().getBool("breast_physics");
			plr.breast_physics_armor = plr.getConfig().getBool("breast_physics_armor");
			plr.show_in_armor = plr.getConfig().getBool("show_in_armor");
			plr.bounceMultiplier = Float.valueOf(plr.getConfig().getParameter("bounce_multiplier").toString());
			plr.floppyMultiplier = Float.valueOf(plr.getConfig().getParameter("floppy_multiplier").toString());

			plr.getBreasts().xOffset = Float.valueOf(plr.getConfig().getParameter("breasts_xOffset").toString());
			plr.getBreasts().yOffset = Float.valueOf(plr.getConfig().getParameter("breasts_yOffset").toString());
			plr.getBreasts().zOffset = Float.valueOf(plr.getConfig().getParameter("breasts_zOffset").toString());
			plr.getBreasts().isUniboob = Boolean.valueOf(plr.getConfig().getParameter("breasts_uniboob").toString());
			plr.getBreasts().cleavage = Float.valueOf(plr.getConfig().getParameter("breasts_cleavage").toString());

			return plr;
		}
		return null;
	}
	
	public static void saveGenderInfo(GenderPlayer plr) {
		plr.getConfig().setParameter("username", plr.username);
		plr.getConfig().setParameter("gender", plr.gender);
		plr.getConfig().setParameter("bust_size", plr.getBustSize());
		plr.getConfig().setParameter("hurt_sounds", plr.hurtSounds);

		//physics
		plr.getConfig().setParameter("breast_physics", plr.breast_physics);
		plr.getConfig().setParameter("breast_physics_armor", plr.breast_physics_armor);
		plr.getConfig().setParameter("show_in_armor", plr.show_in_armor);
		plr.getConfig().setParameter("bounce_multiplier", plr.bounceMultiplier);
		plr.getConfig().setParameter("floppy_multiplier", plr.floppyMultiplier);

		plr.getConfig().setParameter("breasts_xOffset", plr.getBreasts().xOffset);
		plr.getConfig().setParameter("breasts_yOffset", plr.getBreasts().yOffset);
		plr.getConfig().setParameter("breasts_zOffset", plr.getBreasts().zOffset);
		plr.getConfig().setParameter("breasts_uniboob", plr.getBreasts().isUniboob);
		plr.getConfig().setParameter("breasts_cleavage", plr.getBreasts().cleavage);

		plr.getConfig().save();
	}

	public Breasts getBreasts() {
		return breasts;
	}

	public BreastPhysics getLeftBreastPhysics() {
		return lBreastPhysics;
	}
	public BreastPhysics getRightBreastPhysics() {
		return rBreastPhysics;
	}

	public enum SyncStatus {
		CACHED, SYNCED, UNKNOWN
	}
}
