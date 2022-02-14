package org.shalim.wifidirectsender.data;

public interface ITask<I extends ITask.Inputs, O extends ITask.Outputs> {
    public O execute(I inputs);
    public interface Inputs {}
    public interface Outputs {}
}
