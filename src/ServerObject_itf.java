public interface ServerObject_itf {
	public void lock_read(Client_itf client);
	public void lock_write(Client_itf client);
	public void unlock(Client_itf client);
}
