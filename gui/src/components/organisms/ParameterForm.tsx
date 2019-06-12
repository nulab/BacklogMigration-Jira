import React from 'react';
import styled from '@emotion/styled';
import { FieldSet } from '../atoms/FieldSet';
import { Button } from '../atoms/Button';
import { Row } from '../atoms/Row';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Option } from '../atoms/Option';

import { Formik, Field, Form, FormikActions } from 'formik';

const BacklogUrlFieldStyled = styled.div(`
  display: flex;
  align-items: center;
`);

const SpanStyled = styled.span(`
  flex: auto 0 0;
  padding: 0 10px;
`);

const ButtonArea = styled.div(`
  margin-top: 20px;
  text-align: center;
`);

const InputField = styled.div(`
  flex: auto 1 1;
`);

export const ParameterForm = () => (
  <form id='myForm' onSubmit={() => {}}>
    <FieldSet>
      <Row>
        <Label htmlFor='backlogUrl'>Backlog URL</Label>
        <BacklogUrlFieldStyled>
          <SpanStyled>https://</SpanStyled>
          <InputField>
            <Input id='backlogUrl' name='spaceId' value='' tabIndex={1} />
          </InputField>
          <Select name='domain' tabIndex={2}>
            <Option value='.backlog.com' />
            <Option value='.backlog.jp' />
            <Option value='.backlogtool.com' />
          </Select>
        </BacklogUrlFieldStyled>
      </Row>
      <Row>
        <Label htmlFor='apiKey'>API Key</Label>
        <Input id='apiKey' name='apiKey' value='' tabIndex={3} />
      </Row>
      <Row>
        <Label htmlFor='projectKey'>Project Key</Label>
        <Input id='projectKey' name='projectKey' value='' tabIndex={4} />
      </Row>
      <ButtonArea>
        <Button value='Execute' tabIndex={5} />
      </ButtonArea>
    </FieldSet>
  </form>
);
