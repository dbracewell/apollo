package com.davidbracewell.apollo.ml.sequence;

import com.davidbracewell.apollo.ml.Instance;
import com.google.common.base.Preconditions;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * The type Beam decoder.
 *
 * @author David B. Bracewell
 */
public class BeamDecoder implements Decoder {
  private int beamSize;

  /**
   * Instantiates a new Beam decoder.
   */
  public BeamDecoder() {
    this(3);
  }

  /**
   * Instantiates a new Beam decoder.
   *
   * @param beamSize the beam size
   */
  public BeamDecoder(int beamSize) {
    Preconditions.checkArgument(beamSize > 0, "Beam size must be > 0.");
    this.beamSize = beamSize;
  }

  @Override
  public LabelingResult decode(@NonNull SequenceLabeler model, @NonNull Sequence sequence) {
    if (sequence.size() == 0) {
      return new LabelingResult(0);
    }
    MinMaxPriorityQueue<DecoderState> queue = MinMaxPriorityQueue
      .orderedBy(Ordering.natural().reverse())
      .maximumSize(beamSize)
      .create();
    queue.add(new DecoderState(0, null));
    List<DecoderState> newStates = new LinkedList<>();
    ContextualIterator<Instance> iterator = sequence.iterator();
    while (iterator.hasNext()) {
      iterator.next();
      newStates.clear();
      while (!queue.isEmpty()) {
        DecoderState state = queue.remove();
        double[] result = model.estimate(
          iterator.getCurrent().getFeatures().iterator(),
          model.getTransitionFeatures().extract(state)
        );
        for (int i = 0; i < result.length; i++) {
          String label = model.getLabelEncoder().decode(i).toString();
          newStates.add(new DecoderState(state, result[i], label));
        }
      }
      queue.addAll(newStates);
    }

    LabelingResult result = new LabelingResult(sequence.size());
    DecoderState last = queue.remove();
    while (last != null && last.tag != null) {
      result.setLabel(last.index - 1, last.tag, last.stateProbability);
      last = last.previousState;
    }
    queue.clear();
    return result;
  }

  /**
   * Gets beam size.
   *
   * @return the beam size
   */
  public int getBeamSize() {
    return beamSize;
  }

  /**
   * Sets beam size.
   *
   * @param beamSize the beam size
   */
  public void setBeamSize(int beamSize) {
    Preconditions.checkArgument(beamSize > 0, "Beam size must be > 0.");
    this.beamSize = beamSize;
  }

}// END OF BeamDecoder