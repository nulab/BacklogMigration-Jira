import React from 'react';

interface Props {
  id: string;
  name: string;
  value: string;
  tabIndex: number;
  className?: string;
}

export const Input: React.FC<Props> = props => <input type='text' {...props} />;
