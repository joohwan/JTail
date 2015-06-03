package jtail.execute;

/**
 * This class denote the result of command. 
 * @author joohwan.oh
 * @since 13-Aug-08
 */
public class CommandResult {
	private boolean success;
	private final String command;
	private final String stdout;
	private final String stderr;
	public boolean isSuccess() {
		return success;
	}
	public String getCommand() {
		return command;
	}
	public String getStdout() {
		return stdout;
	}
	public String getStderr() {
		return stderr;
	}
	public CommandResult(String command, boolean success, String stdout, String stderr) {		
		this.command = command;
		this.success = success;
		this.stdout = stdout;
		this.stderr = stderr;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getStdouterr() {
		return stdout+stderr;
	}
	
	public String toString(){
		return ("command:"+command+", success:"+success+", stdout:"+stdout+", stderr:"+stderr);
	}
}
