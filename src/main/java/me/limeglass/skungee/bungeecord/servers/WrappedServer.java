package me.limeglass.skungee.bungeecord.servers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.spigot.utils.Utils;

public class WrappedServer {

	private String Xmx = Skungee.getConfiguration("serverinstances").getString("ServerInstances.default-Xmx");
	private String Xms = Skungee.getConfiguration("serverinstances").getString("ServerInstances.default-Xms");
	private InputStream inputStream, errors;
	private ProcessBuilder processBuilder;
	private Boolean isRunning = false;
	private OutputStream outputStream;
	private List<String> commands;
	private Process process;
	private String name;
	private File folder;
	
	public WrappedServer(String template) {
		this.folder = setupFolder(template);
		this.name = template;
		if (this.folder == null) return;
		prepare();
		startup();
		ServerManager.addInstance(this);
	}
	
	//For starting a brand new server and checking if it exists.
	public WrappedServer(String template, String Xmx, String Xms) {
		this.folder = setupFolder(template);
		this.name = template;
		this.Xmx = Xmx;
		this.Xms = Xms;
		if (this.folder == null) return;
		prepare();
		startup();
		ServerManager.addInstance(this);
	}
	
	public WrappedServer(WrappedServer existing) {
		this.commands = existing.getCommands();
		this.name = existing.getServerName();
		this.folder = existing.getFolder();
		this.folder = setupFolder(name);
		this.Xmx = existing.Xmx;
		this.Xms = existing.Xms;
		this.processBuilder = setupProcessBuilder();
		if (this.folder == null) return;
		startup();
		ServerManager.addInstance(this);
	}
	
	public WrappedServer(List<String> commands, String name, String path) {
		this.folder = setupFolder(name);
		this.commands = commands;
		this.name = name;
		this.processBuilder = setupProcessBuilder();
		if (this.folder == null) return;
		startup();
		ServerManager.addInstance(this);
	}
	
	private File setupFolder(String templateName) {
		File folder = new File(ServerManager.getRunningServerFolder() + File.separator + templateName);
		if (folder.exists()) {
			ServerManager.consoleMessage("There was already a server running under the name: " + templateName);
			if (ServerManager.getInstances().containsKey(templateName)) return null;
			folder.delete();
		}
		File template = new File(ServerManager.getTemplateFolder() + File.separator + templateName);
		if (!template.exists() || template.listFiles() == null) {
			ServerManager.consoleMessage("The template: \"" + templateName + "\" was empty or non existant.");
			return null;
		} else {
			this.folder = template;
			if (getJar() == null) {
				ServerManager.consoleMessage("The jar file for template: \"" + templateName + "\" was non existant. Make sure the name matches what is in the serverinstances.yml");
				return null;
			}
		}
		try {
			Utils.copyDirectory(template, folder);
		} catch (IOException exception) {
			Skungee.exception(exception, "Failed to copy the template directory.");
		}
		return folder;
	}
	
	private ProcessBuilder setupProcessBuilder() {
		ServerManager.debugMessage("Command for server " + name + " was created: " + commands.toString());
		return new ProcessBuilder(commands).directory(folder);
	}
	
	public Process getProcess() {
		return process;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	public InputStream getErrorStream() {
		return errors;
	}
	
	public File getFolder() {
		return folder;
	}
	
	public List<String> getCommands() {
		return commands;
	}
	
	public String getServerName() {
		return name;
	}
	
	public String getXmx() {
		return validate(Xmx, "default-Xmx");
	}

	public void setXmx(String xmx) {
		//TODO restart server if this is the case.
		Xmx = xmx;
	}

	public String getXms() {
		return validate(Xms, "default-Xms");
	}

	public void setXms(String xms) {
		//TODO restart server if this is the case.
		Xms = xms;
	}
	
	public Boolean isRunning() {
		return isRunning;
	}

	public void setRunning(Boolean running) {
		if (this.isRunning && !running) shutdown();
		this.isRunning = running;
	}
	
	//Check that the output is logical.
	private String validate(String input, String node) {
		input = input.replaceAll("( |-)", "");
		if (!input.contains("M") || !input.contains("G")) input = Integer.parseInt(input) + "M";
		if (Integer.parseInt(input.replaceAll("(M|G)", "")) < 50) input = Skungee.getConfiguration("serverinstances").getString("ServerInstances." + node, "250M");
		return input;
	}
	
	public File getJar() {
		for (File file : folder.listFiles()) {
			if (file.getName().equalsIgnoreCase(Skungee.getConfiguration("serverinstances").getString("ServerInstances.jar-name", "spigot.jar"))) return file;
		}
		return null;
	}
	
	private void prepare() {
		commands = new ArrayList<String>();
		commands.add("java");
		if (Xmx.matches("(\\-Xmx)(.*)")) {
			commands.add(Xmx);
		} else {
			commands.add("-Xmx" + Xmx);
		}
		if (Xms.matches("(\\-Xms)(.*)")) {
			commands.add(Xms);
		} else {
			commands.add("-Xms" + Xms);
		}
		Boolean isWindows = System.getProperty("os.name").matches("(?i)(.*)(windows)(.*)");
		if (isWindows) commands.add("-Djline.terminal=jline.UnsupportedTerminal");
		for (String command : Skungee.getConfiguration("serverinstances").getStringList("ServerInstances.command-arguments")) {
			commands.add(command);
		}
		commands.add("-jar");
		commands.add(getJar().getName());
		if (isWindows) commands.add("--nojline");
		processBuilder = setupProcessBuilder();
	}
	
	private void startup() {
		if (!isRunning) {
			setRunning(true);
			ServerManager.consoleMessage("Starting up server " + name + "...");
			try {
				process = processBuilder.start();
				inputStream = process.getInputStream();
				errors = process.getErrorStream();
				outputStream = process.getOutputStream();
				ServerManager.getProcesses().add(process);
				//console = new ConsoleWatcher("in", in);
				//err = new ConsoleWatcher("error", errors);
				//new Thread(new ServerWatcher(this, process, outputStream)).start();
			} catch (IOException exception) {
				Skungee.exception(exception, "Failed to start server: " + name);
			}
		}
	}
	
	public void shutdown() {
		if (isRunning) {
			this.isRunning = false;
			ServerManager.consoleMessage("Stopping server \"" + name + "\"...");
			try {
				outputStream.write("stop".getBytes());
				outputStream.flush();
			} catch (IOException exception) {
				Skungee.exception(exception, "Failed to stop server: " + name);
			} finally {
				try {
					outputStream.close();
				}
				catch (IOException e) {}
			}
			ServerManager.getInstances().remove(name);
		}
	}
	
	public void killThreads() {
		//console.killIt();
		//err.killIt();
	}
	
	public void kill() {
		killThreads();
		process.destroy();
		//main.ut.log("Server '" + serverName + "' successfully killed.");
	}
	
	/*public void wrapperStart() {
		if (configuration.getString("ServerInstances.wrapper").equalsIgnoreCase("remote") || configuration.getString("ServerInstances.wrapper").equalsIgnoreCase("default")) {
			String command = "START " + name + " " + ServerManager.getServerInstancesFolder().getAbsolutePath() + " " + getXmx() + " " + getXms() + " " + getJar().getName();
			ProcessBuilder process = new ProcessBuilder(command);
			ServerManager.send(command);
		} else if (Skungee.getConfig().getString("ServerInstances.wrapper").equalsIgnoreCase("screen")) {
			File screen = new File(scripts + "start-screen.sh");
			Object[] arrobject = screen.exists() ? new String[]{"sh", scripts + "start-screen.sh", ID, Skungee.getInstance().getServerManager().getServersFolder().getAbsolutePath(), String.valueOf(this.getXmx()) + "M", String.valueOf(this.getXms()) + "M", this.getJar().getName()} : (!this.getJar().getName().matches("^(?i)spigot.*\\.jar") ? new String[]{"screen", "-dmS", this.getName(), "java", "-Xmx" + this.getXmx() + "M", "-Xms" + this.getXms() + "M", "-jar", this.getJar().getName()} : new String[]{"screen", "-dmS", this.getName(), "java", "-Xmx" + this.getXmx() + "M", "-Xms" + this.getXms() + "M", "-Dcom.mojang.eula.agree=true", "-jar", this.getJar().getName()});
			ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
			processBuilder.command((String[])arrobject);
			processBuilder.directory(this.getServerFolder());
			this.pl.getProcessRunner().queueProcess(this.getName(), processBuilder);
		}
	}*/
}
