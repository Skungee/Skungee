package me.limeglass.skungee.bungeecord.servers;

public class ServerInstance {
	
	//private String scripts = Skungee.getInstance().getDataFolder().getAbsolutePath() + File.separator + "scripts" + File.separator;
	private final String name;
	
	public ServerInstance(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

    /*private void wrapperStart() {
        if (Skungee.getConfig().getString("ServerInstances.wrapper").equalsIgnoreCase("screen")) {
        	File screen = new File(scripts + "start-screen.sh");
            Object[] arrobject = screen.exists() ? new String[]{"sh", scripts + "start-screen.sh", ID, Skungee.getInstance().getServerManager().getServersFolder().getAbsolutePath(), String.valueOf(this.getXmx()) + "M", String.valueOf(this.getXms()) + "M", this.getJar().getName()} : (!this.getJar().getName().matches("^(?i)spigot.*\\.jar") ? new String[]{"screen", "-dmS", this.getName(), "java", "-Xmx" + this.getXmx() + "M", "-Xms" + this.getXms() + "M", "-jar", this.getJar().getName()} : new String[]{"screen", "-dmS", this.getName(), "java", "-Xmx" + this.getXmx() + "M", "-Xms" + this.getXms() + "M", "-Dcom.mojang.eula.agree=true", "-jar", this.getJar().getName()});
            ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
            processBuilder.command((String[])arrobject);
            processBuilder.directory(this.getServerFolder());
            this.pl.getProcessRunner().queueProcess(this.getName(), processBuilder);
        } else if (Skungee.getConfig().getString("ServerInstances.wrapper").equalsIgnoreCase("remote") || Skungee.getConfig().getString("ServerInstances.wrapper").equalsIgnoreCase("default")) {
            String string = "+start " + name + " " + ServerManager.getServerFolder().getAbsolutePath() + " " + this.getXmx() + "M " + this.getXms() + "M " + this.getJar().getName();
            this.pl.getCtrl().send(string);
        }
    }*/
}