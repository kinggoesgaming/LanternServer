package org.lanternpowered.server.network.vanilla.message.type.play;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.data.type.record.RecordType;
import org.lanternpowered.server.network.message.Message;

import java.util.Optional;

import javax.annotation.Nullable;

public final class MessagePlayOutRecord implements Message {

    private final Vector3i position;
    @Nullable private final RecordType recordType;

    public MessagePlayOutRecord(Vector3i position, @Nullable RecordType recordType) {
        this.position = position;
        this.recordType = recordType;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public Optional<RecordType> getRecord() {
        return Optional.ofNullable(this.recordType);
    }
}
